package cn.com.xinli.portal.support;

import cn.com.xinli.portal.core.PortalError;
import cn.com.xinli.portal.core.RemoteException;
import cn.com.xinli.portal.core.credentials.*;
import cn.com.xinli.portal.core.nas.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.*;
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
 * see {@link #reload()}.
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2016/1/25.
 */
@Service
@Transactional(rollbackFor = {DataAccessException.class, RuntimeException.class})
public class NasServiceSupport implements NasService, NasManager, NasLocator, InitializingBean {
    /** Logger. */
    private final Logger logger = LoggerFactory.getLogger(NasServiceSupport.class);

    @Autowired
    private NasStore nasStore;

    private Nas createNasInternal(Nas nas) {
        nasStore.put(nas);
        nasStore.reload();
        return nas;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(nasStore);
    }

    @Override
    public void init() {
        logger.info("Loading NAS/BRAS devices and rules.");
        nasStore.reload();
    }

    @Override
    public void reload() {
        logger.info("NAS/BRAS devices and rules reloading...");
        nasStore.reload();
    }

    @Override
    public Nas get(Long id) throws NasNotFoundException {
        Objects.requireNonNull(id, Nas.EMPTY_NAS);
        return nasStore.get(id);
    }

    @Override
    public Nas findByName(String name) throws NasNotFoundException {
        Optional<Nas> nas = nasStore.devices()
                .filter(n -> n.getName().equals(name))
                .findFirst();
        nas.orElseThrow(() -> new NasNotFoundException("name: " + name));
        return nas.get();
    }

    @Override
    public Nas findByAddress(String ip) throws NasNotFoundException {
        if (StringUtils.isEmpty(ip)) {
            throw new IllegalArgumentException("nas device ip can not be blank.");
        }
        return nasStore.find(ip);
    }

    @Override
    public Nas create(Nas nas) throws NasNotFoundException, RemoteException {
        Objects.requireNonNull(nas, Nas.EMPTY_NAS);

        CredentialsTranslation translation = nas.getTranslation();
        if (translation == null) {
            translation = new CredentialsTranslation();
            translation.setEncoder(new CredentialsEncoders.NoOpEncoder());
            translation.setAuthenticateWithDomain(false);
            translation.setModifiers(Collections.emptyList());
            nas.setTranslation(translation);
        }

        final String ip = nas.getIp();
        try {
            findByAddress(ip);
            throw new RemoteException(PortalError.NAS_IP_OVERLAPPED);
        } catch (NasNotFoundException e) {
            if (logger.isTraceEnabled()) {
                logger.trace("nas ip address checked.");
            }
        }

        return createNasInternal(nas);
    }

    @Override
    public void delete(long id) throws NasNotFoundException {
        nasStore.delete(id);
        nasStore.reload();
    }

    @Override
    public void map(String ip, String mac, String nasIp) throws NasNotFoundException {
        nasStore.map(ip, mac, nasIp);
    }

    @Override
    public Stream<Nas> all() {
        return nasStore.devices();
    }

    @Override
    public void save(Nas nas) {
        nasStore.put(nas);
    }

    @Override
    public Stream<Nas> search(String query) throws RemoteException {
        return nasStore.search(query);
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
        Objects.requireNonNull(credentials, Credentials.EMPTY_CREDENTIALS);

        try {
            return nasStore.locate(Pair.of(credentials.getIp(), credentials.getMac()));
        } catch (NasNotFoundException e) {
            if (logger.isTraceEnabled()) {
                logger.trace("incoming request not mapped (through web redirect), trying rules.");
            }
            Optional<NasRule> rule = nasStore.rules()
                    .filter(r -> r.matches(credentials))
                    .findFirst();

            rule.orElseThrow(() -> e);

            logger.trace("rule hit, nas id: {}", rule.get().getNas().getId());

            return rule.get().getNas();
        }
    }

    @Override
    public NasRule createNasDomainRule(Nas nas, String[] domains) {
        DomainBasedNasRule rule = new DomainBasedNasRule();
        String supportedDomains = Stream.of(domains).collect(Collectors.joining(","));
        rule.setSupportedDomains(supportedDomains);
        rule.setNas(nas);
        nasStore.put(rule);

        return rule;
    }

    @Override
    public NasRule createNasIpv4RangeRule(Nas nas, String startIp, String endIp) {
        IPv4RangeBasedNasRule rule = new IPv4RangeBasedNasRule();
        rule.setIpv4start(startIp);
        rule.setIpv4end(endIp);
        rule.setNas(nas);
        nasStore.put(rule);

        return rule;
    }

    @Override
    public Nas createHuaweiNas(NasConfig nasConfig) {
        List<CredentialsModifier> modifiers = new ArrayList<>();

        for (NasConfig.ModifierConfig m : nasConfig.getModifiers()) {
            CredentialsModifier modifier = new CredentialsModifier();
            modifier.setPosition(CredentialsModifier.Position.valueOf(m.getPosition()));
            modifier.setTarget(CredentialsModifier.Target.valueOf(m.getTarget()));
            modifier.setValue(m.getValue());
            modifiers.add(modifier);
        }

        CredentialsEncoder noOp = new CredentialsEncoders.NoOpEncoder();

        CredentialsTranslation translation = new CredentialsTranslation();
        translation.setModifiers(modifiers);
        translation.setAuthenticateWithDomain(nasConfig.isAuthenticateWithDomain());
        translation.setEncoder(noOp);

        HuaweiNas huaweiNas = new HuaweiNas();
        huaweiNas.setName(nasConfig.getName());
        huaweiNas.setAuthType(nasConfig.getAuthType());
        huaweiNas.setListenPort(nasConfig.getPort());
        huaweiNas.setIpv4Address(nasConfig.getHost());
        huaweiNas.setSharedSecret(nasConfig.getSharedSecret());
        huaweiNas.setVersion(nasConfig.getVersion());
        huaweiNas.setTranslation(translation);
        Nas nas = createNasInternal(huaweiNas);

        if (logger.isDebugEnabled()) {
            logger.debug("nas created: {}", nas);
        }

        return nas;
    }
}
