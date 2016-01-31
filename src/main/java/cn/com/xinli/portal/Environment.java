package cn.com.xinli.portal;

import cn.com.xinli.portal.core.certificate.Certificate;
import cn.com.xinli.portal.core.certificate.CertificateManager;
import cn.com.xinli.portal.core.certificate.CertificateNotFoundException;
import cn.com.xinli.portal.core.certificate.CertificateService;
import cn.com.xinli.portal.core.configuration.PortalServerConfiguration;
import cn.com.xinli.portal.core.configuration.support.PropertiesServerConfiguration;
import cn.com.xinli.portal.core.credentials.CredentialsEncoder;
import cn.com.xinli.portal.core.credentials.CredentialsEncoders;
import cn.com.xinli.portal.core.credentials.CredentialsModifier;
import cn.com.xinli.portal.core.credentials.CredentialsTranslation;
import cn.com.xinli.portal.core.nas.*;
import cn.com.xinli.portal.transport.PortalServer;
import cn.com.xinli.portal.transport.huawei.AuthType;
import cn.com.xinli.portal.transport.huawei.Endpoint;
import cn.com.xinli.portal.transport.huawei.Version;
import cn.com.xinli.portal.transport.huawei.nio.HuaweiPortal;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;

import java.util.Collections;

/**
 * Portal runtime environment.
 *
 * Project: xpws
 *
 * @author zhoupeng 2016/1/31.
 */
@Configuration
@Order(Stage.SERVE)
public class Environment {
    /** Logger. */
    private final Logger logger = LoggerFactory.getLogger(Environment.class);

    @Autowired
    private NasManager nasManager;

    @Autowired
    private NasService nasService;

    @Autowired
    private CertificateManager certificateManager;

    @Autowired
    private CertificateService certificateService;

    @Autowired
    private PropertiesServerConfiguration propertiesServerConfiguration;

    /**
     * Handle spring-context refreshed event.
     *
     * <p>Server loads NAS/BRAS devices only after spring-context refreshed, otherwise
     * server may encounter 'no session' lazy-initialization exception when access lazy-initial
     * JPA entities, such as {@link Nas}.
     *
     * <p>Server also creates mock-huawei-bras after NAS/BRAS devices already loaded
     * (by calling {@link NasService#init()}.
     *
     * @param event spring-context refreshed event.
     */
    @EventListener
    @Order(Stage.SERVE)
    public void handleContextRefresh(ContextRefreshedEvent event) throws Exception {
        logger.info("context refresh event: {}", event);
        nasService.init();

        PortalServerConfiguration mockNas;
//        try {
        mockNas = propertiesServerConfiguration.getMockNas();

        if (mockNas != null) {
            String name = mockNas.getName();
            ensureNasConfiguration(name);
                /* Find nas configuration for mocking. */
            HuaweiNas nas = (HuaweiNas) nasService.find(name);

            Endpoint endpoint = new Endpoint();
            endpoint.setPort(nas.getListenPort());
            endpoint.setSharedSecret(nas.getSharedSecret());
            endpoint.setVersion(Version.valueOf(nas.getVersion()));
            endpoint.setAddress(nas.getNetworkAddress());

            PortalServer server = HuaweiPortal.createNas(endpoint);
            server.start();
        }
//        } catch (ServerException e) {
//            logger.error("Failed to load configuration", e);
//            logger.error("Server error: {}, {}", e.getPortalError().getValue(), e.getPortalError().getReason());
//            System.exit(e.getPortalError().getValue());
//        }
    }

    /**
     * Create 'jportal' client certificate if missing.
     * @return 'jportal' client certificate.
     */
    @Bean
    Certificate jPortalCertificate() {
        try {
            return certificateService.loadCertificate("jportal");
        } catch (CertificateNotFoundException e) {
            logger.debug("jportal not exist");
            return certificateManager.create("jportal", "xinli", "android", "v1.0", "s3cr3t");
        }
    }

    /**
     * Create NAS/BRAS device for given name.
     *
     * <p>If HUAWEI NAS is enabled for developing purpose,
     * This bean will also create a {@link PortalServer} for HUAWEI NAS.
     *
     * @param nasName NAS/BRAS device name.
     * @return NAS.
     */
    private Nas createNas(String nasName) {
        CredentialsModifier domain = new CredentialsModifier();
        domain.setPosition(CredentialsModifier.Position.TAIL);
        domain.setTarget(CredentialsModifier.Target.USERNAME);
        domain.setValue("@xinli");

        CredentialsEncoder noOp = new CredentialsEncoders.NoOpEncoder();

        CredentialsTranslation translation = new CredentialsTranslation();
        translation.setModifiers(Collections.singletonList(domain));
        translation.setEncoder(noOp);

        HuaweiNas huaweiNas = new HuaweiNas();
        huaweiNas.setName(nasName);
        huaweiNas.setAuthType(AuthType.CHAP.name());
        huaweiNas.setListenPort(2000);
        huaweiNas.setIpv4Address("127.0.0.1");
        huaweiNas.setSharedSecret("s3cr3t");
        huaweiNas.setVersion(Version.V2.name());

        huaweiNas.setTranslation(translation);

        Nas nas = nasManager.create(huaweiNas);

//        Nas nas = nasManager.create(nasName, "127.0.0.1", null, NasType.HuaweiV2,
//                2000, AuthType.CHAP, "s3cr3t", translation);

        if (logger.isDebugEnabled()) {
            logger.debug("nas created: {}", nas);
        }

        return nas;
    }

    /**
     * Ensure given NAS/BRAS device exists.
     *
     * <p>If NAS/BRAS device with given name not exists, create new one.
     * @param nasName NAS/BRAS device name.
     */
    private void ensureNasConfiguration(String nasName) {
        if (StringUtils.isEmpty(nasName)) {
            throw new IllegalArgumentException("NAS name can not be blank.");
        }

        try {
            Nas nas = nasService.find(nasName);
            logger.debug("NAS found, {}", nas);
        } catch (NasNotFoundException e) {
            logger.debug("NAS not found, name: {}", nasName);
            Nas nas = createNas(nasName);
            nasManager.createNasIpv4RangeRule(nas, "192.168.3.1", "192.168.3.254");
            nasService.reload();
        }
    }
}
