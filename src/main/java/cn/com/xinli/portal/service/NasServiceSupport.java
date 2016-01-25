package cn.com.xinli.portal.service;

import cn.com.xinli.portal.core.*;
import cn.com.xinli.portal.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
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
 * NAS information does not exists in the {@link #userNasMapping}. PWS should
 * try those pre-defined rules to match incoming requests after mapping lookup.
 *
 * <p>This class provides several methods for retrieving NAS entities, includes
 * by <tt>ip, mac</tt>, by <tt>domain</tt>, by <tt>ip address range</tt>.
 *
 * Project: xpws
 *
 * @author zhoupeng 2016/1/25.
 */
@Service
@Transactional(rollbackFor = DataAccessException.class)
public class NasServiceSupport implements NasService, NasManager, InitializingBean {
    /** Logger. */
    private final Logger logger = LoggerFactory.getLogger(NasServiceSupport.class);

    @Autowired
    private NasRuleRepository nasRuleRepository;

    @Autowired
    private NasRepository nasRepository;

    @Autowired
    private CredentialsEncoderRepository credentialsEncoderRepository;

    @Autowired
    private CredentialsModifierRepository credentialsModifierRepository;

    @Autowired
    private CredentialsTranslationRepository credentialsTranslationRepository;

    /** NAS/BRAS devices, key: nas id, value: nas configuration. */
    private final Map<Long, Nas> devices = Collections.synchronizedMap(new HashMap<>());

    /** User, Nas mapping, key: user info pair, value: nas id. */
    private final Map<String, Long> userNasMapping = new ConcurrentHashMap<>();

    /** Additional Rules for nas matching. */
    private List<NasRule> nasRules = new ArrayList<>();

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(nasRuleRepository);
    }

    @PostConstruct
    public void init() {
        logger.info("Loading NAS/BRAS devices and rules.");
        load();
    }

    @Override
    public void reload() {
        logger.info("NAS/BRAS devices and rules reloading...");
        nasRules.clear();
        devices.clear();
        load();
    }

    private void load() {
        nasRuleRepository.findAll().forEach(r -> nasRules.add(r));
        nasRepository.findAll().forEach(n -> devices.put(n.getId(), n));
    }

    private Optional<Nas> getInternal(long id) {
        return Optional.ofNullable(devices.get(id));
    }

    @Override
    public Nas get(long id) throws NasNotFoundException {
        Optional<Nas> nas = getInternal(id);
        nas.orElseThrow(() -> new NasNotFoundException("id: " + id));
        return nas.get();
    }

    private Optional<Nas> getInternal(String ip) {
        return devices.values().stream()
                .filter(n -> n.getIp().equals(ip))
                .findFirst();
    }

    @Override
    public Nas get(String ip) throws NasNotFoundException {
        Optional<Nas> nas = getInternal(ip);
        nas.orElseThrow(() -> new NasNotFoundException("ip: " + ip));
        return nas.get();
    }

    @Override
    public Nas find(String name) throws NasNotFoundException {
        Optional<Nas> nas = devices.values().stream()
                .filter(n -> n.getName().equals(name))
                .findFirst();
        nas.orElseThrow(() -> new NasNotFoundException("name: " + name));
        return nas.get();
    }

    @Override
    public Nas createNas(String name,
                         String ipv4Address,
                         String ipv6Address,
                         NasType type,
                         int listenPort,
                         AuthType authType,
                         String sharedSecret,
                         CredentialsTranslation translation) {
        Nas nas = new Nas();
        nas.setName(name);
        nas.setIpv4Address(ipv4Address);
        nas.setIpv6Address(ipv6Address);
        nas.setType(type);
        nas.setListenPort(listenPort);
        nas.setSharedSecret(sharedSecret);
        nas.setAuthType(authType);
        nas.setTranslation(translation);

        CredentialsEncoder encoder = translation.getEncoder();
        if (encoder != null) {
            credentialsEncoderRepository.save(translation.getEncoder());
        }

        if (!translation.isEmpty()) {
            translation.getModifiers().forEach(m -> credentialsModifierRepository.save(m));
        }

        credentialsTranslationRepository.save(translation);

        return nasRepository.save(nas);
    }

    @Override
    public void deleteNas(long id) {
        nasRepository.delete(id);
    }

    @Override
    public void map(String userKey, String nasKey) throws NasNotFoundException {
        Nas nas = get(nasKey);
        userNasMapping.put(userKey, nas.getId());
    }

    /**
     * Locate NAS for given user ip and user mac.
     *
     * @param ip  user ip address.
     * @param mac user mac address.
     * @return NAS matches ip and mac, or null if not found.
     */
    private Optional<Nas> locate(String ip, String mac) {
        String pair = Session.pair(ip, mac);
        Long nasId = userNasMapping.get(pair);

        return nasId == null ? Optional.empty() : getInternal(nasId);
    }

    /**
     * {@inheritDoc}
     *
     * <p>Find user incoming NAS.
     * Try finding NAS in the {@link #userNasMapping}, if not found,
     * try fail-safe rules then.
     *
     * @param credentials user credentials.
     * @return NAS if found.
     */
    @Override
    public Nas locate(Credentials credentials) throws NasNotFoundException {
        Objects.requireNonNull(credentials);

        Optional<Nas> nas = locate(credentials.getIp(), credentials.getMac());
        if (!nas.isPresent()) {
            if (logger.isTraceEnabled()) {
                logger.trace("incoming request not mapped (through web redirect), trying ip range.");
            }

            Optional<NasRule> rule = nasRules.stream()
                    .filter(r -> r.matches(credentials))
                    .findFirst();

            if (rule.isPresent()) {
                return rule.get().getNas();
            }
            throw new NasNotFoundException("ip:" + credentials.getIp() + ", mac: " + credentials.getMac());
        } else {
            return nas.get();
        }
    }

    @Override
    public NasRule createNasDomainRule(Nas nas, String[] domains) {
        DomainBasedNasRule rule = new DomainBasedNasRule();
        String supportedDomains = Stream.of(domains).collect(Collectors.joining(","));
        rule.setSupportedDomains(supportedDomains);
        rule.setNas(nas);
        return nasRuleRepository.save(rule);
    }

    @Override
    public NasRule createNasIpv4RangeRule(Nas nas, String startIp, String endIp) {
        IPv4RangeBasedNasRule rule = new IPv4RangeBasedNasRule();
        rule.setIpv4start(startIp);
        rule.setIpv4end(endIp);
        rule.setNas(nas);
        return nasRuleRepository.save(rule);
    }
}
