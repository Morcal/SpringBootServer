package cn.com.xinli.portal;

import cn.com.xinli.portal.core.certificate.Certificate;
import cn.com.xinli.portal.core.certificate.CertificateManager;
import cn.com.xinli.portal.core.certificate.CertificateNotFoundException;
import cn.com.xinli.portal.core.certificate.CertificateService;
import cn.com.xinli.portal.core.nas.*;
import cn.com.xinli.portal.transport.PortalServer;
import cn.com.xinli.portal.transport.huawei.AuthType;
import cn.com.xinli.portal.transport.huawei.Endpoint;
import cn.com.xinli.portal.transport.huawei.Version;
import cn.com.xinli.portal.transport.huawei.support.HuaweiPortal;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * Develop, testing environment.
 *
 * <p>This configuration only works if spring-boot configured "dev"
 * as one of active profiles. It can be done by edit "application.properties"
 * or programmatically. See
 * <a href="https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-profiles.html">
 * spring boot profile documents</a>.
 *
 * <p>In a developing environment, A mocked Huawei portal NAS will be created
 * and listen on local address for incoming portal requests. And a certificate with
 * name "jportal" will be generated.
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2016/2/1.
 */
@Configuration
@Order(Stage.SERVE)
@Profile("dev")
@PropertySource("dev.properties")
public class DevEnvironment {
    /** Logger. */
    private final Logger logger = LoggerFactory.getLogger(DevEnvironment.class);

    @Value("${mock.nas.host}")
    private String mockNasHost;

    @Value("${mock.nas.name}")
    private String mockNasName;

    @Value("${mock.nas.port}")
    private int mockNasPort;

    @Value("${mock.nas.shared-secret}")
    private String mockNasSharedSecret;

    @Value("${mock.nas.version}")
    private String mockNasVersion;

    @Value("${mock.nas.auth-type}")
    private String mockNasAuthType;

    @Value("${mock.nas.ipv4.range}")
    private String mockNasIpv4Ranges;

    @Value("${mock.nas.enable}")
    private boolean enableMock;

    @Value("${mock.nas.authenticateWithDomain}")
    private boolean mockNasAuthenticateWithDomain;

//    /** NAS configuration filename prefix. */
//    public static final String NAS_CONFIG_FILENAME_PREFIX = "nas";

    @Autowired
    private NasService nasService;

    @Autowired
    private NasManager nasManager;

    @Autowired
    private CertificateManager certificateManager;

    @Autowired
    private CertificateService certificateService;

    /**
     * Handle PWS environment initialized event.
     *
     * <p>create internal HUAWEI nas and ensure 'jportal' client certificate exists.
     *
     * @param event PWS environment initialized event.
     * @throws NasNotFoundException
     * @throws IOException
     */
    @EventListener
    public void handleEnvironmentInitialized(EnvironmentInitializedEvent event)
            throws NasNotFoundException, IOException {
        logger.info("PWS environment initialized, {}", event);

        ensureJPortalCertificate();

        //ensureNasConfigurations(NAS_CONFIG_FILENAME_PREFIX, 1, 1);

        if (enableMock) {
            NasConfig mock = new NasConfig();
            mock.setName(mockNasName);
            mock.setPort(mockNasPort);
            mock.setAuthType(mockNasAuthType);
            mock.setHost(mockNasHost);
            mock.setSharedSecret(mockNasSharedSecret);
            mock.setVersion(mockNasVersion);
            mock.setRange(mockNasIpv4Ranges);
            mock.setAuthenticateWithDomain(mockNasAuthenticateWithDomain);
            ensureHuaweiNasConfiguration(mock);

            /* Find nas configuration for mocking. */
            HuaweiNas nas = (HuaweiNas) nasService.find(mockNasName);

            Endpoint endpoint = new Endpoint();
            endpoint.setPort(nas.getListenPort());
            endpoint.setSharedSecret(nas.getSharedSecret());
            endpoint.setVersion(Version.valueOf(nas.getVersion()));
            endpoint.setAddress(nas.getNetworkAddress());
            endpoint.setAuthType(AuthType.valueOf(nas.getAuthType()));

            PortalServer mockServer = HuaweiPortal.createNas(endpoint);
            mockServer.start();
        }
    }

    /**
     * Create 'jportal' client certificate if missing.
     *
     * @return 'jportal' client certificate.
     */
    private Certificate ensureJPortalCertificate() {
        try {
            return certificateService.loadCertificate("jportal");
        } catch (CertificateNotFoundException e) {
            logger.debug("jportal not exist");
            return certificateManager.create("jportal", "xinli", "android", "v1.0", "s3cr3t");
        }
    }

    /**
     * Ensure given NAS/BRAS device exists.
     *
     * <p>If NAS/BRAS device with given name not exists, create new one.
     *
     * @param nasConfig NAS/BRAS device config.
     */
    private void ensureHuaweiNasConfiguration(NasConfig nasConfig) {
        if (StringUtils.isEmpty(nasConfig.getName())) {
            throw new IllegalArgumentException("NAS name can not be blank.");
        }

        try {
            Nas nas = nasService.find(nasConfig.getName());
            logger.debug("NAS found, {}", nas);
        } catch (NasNotFoundException e) {
            logger.debug("NAS not found, creating... name: {}", nasConfig);
            Nas nas = nasManager.createHuaweiNas(nasConfig);
            if (!StringUtils.isEmpty(nasConfig.getRange())) {
                for (String range : nasConfig.getRange().split(",")) {
                    String[] value = range.trim().split("-");
                    if (value.length == 2) {
                        nasManager.createNasIpv4RangeRule(nas, value[0], value[1]);
                    }
                }
            }
            nasService.reload();
        }
    }

    @Deprecated
    private void ensureNasConfigurations(String prefix, int start, int step) {
        String configFilename;
        Set<String> names = new HashSet<>();

        for (int i = start; ; i += step) {
            configFilename = prefix + '-' + i + ".properties";
            InputStream in;

            in = getClass().getClassLoader().getResourceAsStream(configFilename);
            if (in == null)
                break;

            logger.info("loading NAS configuration from file: {}", configFilename);

            try {
                Properties properties = new Properties();
                properties.load(in);
                if (!properties.getProperty("nas.enable").equalsIgnoreCase("TRUE")) {
                    logger.info("NAS configuration {} is disabled, ignored.", configFilename);
                } else {
                    final NasConfig config = new NasConfig();
                    config.load(properties);

                    if (!names.add(config.getName())) {
                        in.close();
                        logger.error("Redundant configuration name: {}, detected in file: {}",
                                config.getName(), configFilename);
                        System.exit(1);
                    }

                    ensureHuaweiNasConfiguration(config);
                }
                in.close();
            } catch (IOException e) {
                logger.error("Failed to load NAS configuration: {}", configFilename, e);
            } finally {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
