package cn.com.xinli.portal;

import cn.com.xinli.portal.core.certificate.Certificate;
import cn.com.xinli.portal.core.certificate.CertificateManager;
import cn.com.xinli.portal.core.certificate.CertificateNotFoundException;
import cn.com.xinli.portal.core.certificate.CertificateService;
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
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Collections;

/**
 * Develop, testing environment.
 *
 * <p>This configuration only works if spring-boot configured "dev"
 * as one of active profiles. It can be done by edit "application.properties"
 * or programmatically. See
 * <a href="https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-profiles.html">
 *     spring boot profile documents</a>.
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2016/2/1.
 */
@Configuration
@Order(Stage.SERVE)
@Profile("dev")
public class DevEnvironment {
    /** Logger. */
    private final Logger logger = LoggerFactory.getLogger(DevEnvironment.class);

    /** Configuration options for Developing, Testing. */
    public static final String MOCK_NAS_HOST = "127.0.0.1";
    public static final String MOCK_NAS_NAME = "mock-huawei-nas";
    public static final int MOCK_NAS_LISTEN_PORT = 2000;
    public static final String MOCK_NAS_SHARED_SECRET = "s3cr3t";
    public static final Version MOCK_NAS_VERSION = Version.V2;
    public static final AuthType MOCK_NAS_AUTH_TYPE = AuthType.CHAP;

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
        PortalServer huaweiNas = createHuaweiNas();
        huaweiNas.start();
    }

    PortalServer createHuaweiNas() throws NasNotFoundException, UnknownHostException {
        ensureNasConfiguration(MOCK_NAS_NAME);
        /* Find nas configuration for mocking. */
        HuaweiNas nas = (HuaweiNas) nasService.find(MOCK_NAS_NAME);

        Endpoint endpoint = new Endpoint();
        endpoint.setPort(nas.getListenPort());
        endpoint.setSharedSecret(nas.getSharedSecret());
        endpoint.setVersion(Version.valueOf(nas.getVersion()));
        endpoint.setAddress(nas.getNetworkAddress());

        return HuaweiPortal.createNas(endpoint);
    }

    /**
     * Create 'jportal' client certificate if missing.
     * @return 'jportal' client certificate.
     */
    Certificate ensureJPortalCertificate() {
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
     * This method will create a {@link PortalServer} for HUAWEI NAS.
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
        huaweiNas.setAuthType(MOCK_NAS_AUTH_TYPE.name());
        huaweiNas.setListenPort(MOCK_NAS_LISTEN_PORT);
        huaweiNas.setIpv4Address(MOCK_NAS_HOST);
        huaweiNas.setSharedSecret(MOCK_NAS_SHARED_SECRET);
        huaweiNas.setVersion(MOCK_NAS_VERSION.name());
        huaweiNas.setTranslation(translation);
        Nas nas = nasManager.create(huaweiNas);

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