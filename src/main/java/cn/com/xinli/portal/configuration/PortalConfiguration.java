package cn.com.xinli.portal.configuration;

import cn.com.xinli.portal.*;
import cn.com.xinli.portal.auth.AuthorizationServer;
import cn.com.xinli.portal.auth.CertificateService;
import cn.com.xinli.portal.protocol.huawei.DefaultPortalServer;
import cn.com.xinli.portal.protocol.huawei.HuaweiNas;
import cn.com.xinli.portal.rest.auth.RestAuthorizationServer;
import cn.com.xinli.portal.support.ActivityServiceSupport;
import cn.com.xinli.portal.support.CertificateServiceSupport;
import cn.com.xinli.portal.support.NasMappingSupport;
import cn.com.xinli.portal.support.SessionServiceSupport;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

import java.io.IOException;

/**
 * PWS configuration.
 *
 * <p>This configuration should be able to run without
 * any REST related configurations or implementations.
 * I.e. running as a web-page-only server.
 * </p>
 *
 * <p><strong>DO NOT introduce any dependencies from REST packages.</strong>
 * </p>
 *
 * Project: xpws.
 *
 * @author zhoupeng 2015/12/6.
 */
@Configuration
@ImportResource("classpath:nas.xml")
public class PortalConfiguration {
    /** Log. */
    private static final Log log = LogFactory.getLog(PortalConfiguration.class);

    @Value("${pws.root}") private String application;

    @Value("${pws.private_key}") private String privateKey;

    @Value("${pws.session.keepalive.requires}") private boolean keepalive;

    @Value("${pws.session.keepalive.interval}") private int keepaliveInterval;

    @Value("${pws.database.derby.scheme}") private String derbyScheme;

    @Value("${pws.database.derby.mem.enable}") private boolean derbyMemDb;

    @Value("${pws.database.init.sql}") private String initSql;

    @Value("${pws.portal.server.listen.port}") private int portalServerListenPort;

    @Value("${pws.portal.server.thread.size}") private int portalServerThreadSize;

    @Value("${pws.portal.server.shared_secret}") private String portalServerSharedSecret;

    @Value("${pws.portal.server.huawei.version}") private String portalServerHuaweiVersion;

    @Value("${pws.rest.scheme}") private String restSchemeScheme;

    @Value("${pws.rest.server}") private String restSchemeServer;

    @Value("${pws.rest.host}") private String restSchemeHost;

    @Value("${pws.rest.port}") private int restSchemePort;

    @Value("${pws.rest.version}") private String restSchemeVersion;

    @Value("${pws.nas.huawei.mock.enable}") private boolean mockHuaweiNas;

    @Bean
    public ServerConfig serverConfig() {
        ServerConfig config = new ServerConfig();
        config.setUseDerbyMemDb(derbyMemDb);
        config.setDerbyScheme(derbyScheme);
        config.setInitSql(initSql);
        config.setRequiresKeepalive(keepalive);
        config.setKeepaliveInterval(keepaliveInterval);
        config.setPrivateKey(privateKey);
        config.setApplication(application);
        config.setPortalServerListenPort(portalServerListenPort);
        config.setPortalServerHuaweiVersion(portalServerHuaweiVersion);
        config.setPortalServerSharedSecret(portalServerSharedSecret);
        config.setPortalServerThreadSize(portalServerThreadSize);
        config.setRestSchemeHost(restSchemeHost);
        config.setRestSchemePort(restSchemePort);
        config.setRestSchemeScheme(restSchemeScheme);
        config.setRestSchemeServer(restSchemeServer);
        config.setRestSchemeVersion(restSchemeVersion);
        return config;
    }

    @Bean
    public Activity.Severity minimalSeverity() {
        return Activity.Severity.NORMAL;
    }

    @Bean
    public SessionService sessionService() {
        return new SessionServiceSupport();
    }

    @Bean
    public AuthorizationServer authorizationServer() {
        return new RestAuthorizationServer();
    }

    @Bean
    public ActivityService activityService() {
        return new ActivityServiceSupport();
    }

    @Bean
    public NasMapping deviceMapping() {
        return new NasMappingSupport();
    }

    @Bean
    public CertificateService certificateService() {
        return new CertificateServiceSupport();
    }

    @Bean(initMethod = "start")
    public DefaultPortalServer portalServer() {
        if (mockHuaweiNas) {
            HuaweiNas nas = new HuaweiNas();
            try {
                nas.start();
            } catch (IOException e) {
                log.error("Failed to start mock huawei nas", e);
            }
        }
        return new DefaultPortalServer(serverConfig(), sessionService());
    }
}
