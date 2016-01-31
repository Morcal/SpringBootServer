package cn.com.xinli.portal.support;

import cn.com.xinli.portal.core.credentials.Credentials;
import cn.com.xinli.portal.core.credentials.CredentialsTranslation;
import cn.com.xinli.portal.core.nas.*;
import cn.com.xinli.portal.support.persist.NasPersistence;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.persistence.FetchType;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * PWS {@link Nas} mapping.
 *
 * <p>This class implements NAS mappings.
 * It saves client/user information (mainly ip and mac) and which {@link Nas}
 * it came from. When clients/users require to create portal connection,
 * PWS retrieves NAS information from this mapping and then communicate with
 * NAS/BRAS which be found {@link Nas}.
 *
 * <p>This class also provides fail-safe {@link NasRule}s in case that
 * NAS information does not exists in the {@link #nasStore}. PWS should
 * try those pre-defined rules to match incoming requests after mapping lookup.
 *
 * <p>This class provides several methods for retrieving NAS entities, includes
 * by <tt>ip, mac</tt>, by <tt>domain</tt>, by <tt>ip address range</tt>.
 *
 * <p>Loading {@link NasRule}s and {@link Nas} devices should be handled carefully.
 * Because the central entity, {@link Nas}'s details are lazy-initial fetched.
 * see {@link #load()}.
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2016/1/25.
 */
@Service
@Transactional(rollbackFor = DataAccessException.class)
public class NasServiceSupport implements NasService, NasManager, NasLocator, InitializingBean {
    /** Logger. */
    private final Logger logger = LoggerFactory.getLogger(NasServiceSupport.class);

    @Autowired
    private NasPersistence nasPersistence;

    @Autowired
    private NasStore nasStore;

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(nasPersistence);
    }

    @Override
    public void init() {
        logger.info("Loading NAS/BRAS devices and rules.");
        load();
    }

    @Override
    public void reload() {
        logger.info("NAS/BRAS devices and rules reloading...");
        load();
    }

    /**
     * Load all NAS/BRAS devices and rules for those devices.
     *
     * <p>Since {@link NasRule}s fetch associated {@link Nas} with
     * {@link FetchType#EAGER} mode and {@link Nas}'s
     * {@link CredentialsTranslation} will be fetched by {@link FetchType#LAZY}
     * mode, we need force {@link #nasPersistence} to load NAS details with
     * lazy-loading.
     *
     * <p>Results of calling this method are:
     * <br>{@link NasRule}s are loaded with <em>partial</em> {@link Nas} into
     * {@link #nasStore}, {@link Nas#translation} is not loaded yet, but its sufficient for server.
     * <br>{@link Nas}s are populated with <em>full-loaded</em> {@link Nas}es and
     * saved in {@link #nasStore}.
     */
    private void load() {
        nasPersistence.rules().forEach(this::addRule);
        /* Force lazy-loading in #addDevice. */
        nasPersistence.devices().forEach(this::addDevice);
    }

    private void addRule(NasRule rule) {
        nasStore.put(rule);
    }

    /**
     * Add a NAS/BRAS device.
     *
     * <p>By calling on {@link Nas#getTranslation()} and {@link CredentialsTranslation#getModifiers()}
     * methods, force lazy-initialization to load details of {@link Nas}es.
     *
     * @param device NAS/BRAS device.
     */
    private void addDevice(Nas device) {
        Objects.requireNonNull(device);
        CredentialsTranslation translation = device.getTranslation();
        /*
         * Call on detail information to force lazy-loading.
         */
        translation.getModifiers().size();
        logger.info("adding device: {}, translation: {}", device, translation);
        nasStore.put(device);
    }

    @Override
    public Nas get(String ip) throws NasNotFoundException {
        if (StringUtils.isEmpty(ip)) {
            throw new IllegalArgumentException("nas ip can not be empty.");
        }
        return nasStore.get(ip);
    }

    @Override
    public Nas find(String name) throws NasNotFoundException {
        Optional<Nas> nas = nasStore.devices()
                .filter(n -> n.getName().equals(name))
                .findFirst();
        nas.orElseThrow(() -> new NasNotFoundException("name: " + name));
        return nas.get();
    }

    @Override
    public Nas create(Nas nas) {
        nasPersistence.save(nas);
        nasStore.put(nas);
        return nas;
    }

    @Override
    public void delete(Nas nas) throws NasNotFoundException {
        nasStore.delete(nas.getIp());
    }

    @Override
    public void map(String ip, String mac, String nasIp) throws NasNotFoundException {
        nasStore.map(ip, mac, nasIp);
    }

    /**
     * {@inheritDoc}
     *
     * <p>Find user incoming NAS.
     * Try finding NAS in the {@link #nasStore}, if not found,
     * try fail-safe rules then.
     *
     * @param credentials user credentials.
     * @return NAS if found.
     */
    @Override
    public Nas locate(Credentials credentials) throws NasNotFoundException {
        Objects.requireNonNull(credentials);

        try {
            return nasStore.locate(credentials);
        } catch (NasNotFoundException e) {
            if (logger.isTraceEnabled()) {
                logger.trace("incoming request not mapped (through web redirect), trying rules.");
            }
            Optional<NasRule> rule = nasStore.rules()
                    .filter(r -> r.matches(credentials))
                    .findFirst();

            rule.orElseThrow(() -> e);

            return rule.get().getNas();
        }
    }

    @Override
    public NasRule createNasDomainRule(Nas nas, String[] domains) {
        DomainBasedNasRule rule = new DomainBasedNasRule();
        String supportedDomains = Stream.of(domains).collect(Collectors.joining(","));
        rule.setSupportedDomains(supportedDomains);
        rule.setNas(nas);
        nasPersistence.save(rule);
        nasStore.put(rule);

        return rule;
    }

    @Override
    public NasRule createNasIpv4RangeRule(Nas nas, String startIp, String endIp) {
        IPv4RangeBasedNasRule rule = new IPv4RangeBasedNasRule();
        rule.setIpv4start(startIp);
        rule.setIpv4end(endIp);
        rule.setNas(nas);
        nasPersistence.save(rule);
        nasStore.put(rule);

        return rule;
    }
}
