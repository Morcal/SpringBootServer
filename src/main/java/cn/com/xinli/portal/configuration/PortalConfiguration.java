package cn.com.xinli.portal.configuration;

import cn.com.xinli.portal.*;
import cn.com.xinli.portal.auth.AuthorizationServer;
import cn.com.xinli.portal.auth.CertificateService;
import cn.com.xinli.portal.rest.auth.RestAuthorizationServer;
import cn.com.xinli.portal.support.ActivityServiceSupport;
import cn.com.xinli.portal.support.CertificateServiceSupport;
import cn.com.xinli.portal.support.NasMappingSupport;
import cn.com.xinli.portal.support.SessionServiceSupport;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

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

    @Value("${pws.root}") private String application;

    @Value("${pws.private_key}") private String privateKey;

    @Value("${pws.session.requiresKeepalive}") private boolean keepalive;

    @Value("${pws.session.requiresKeepalive.interval}") private int keepaliveInterval;

    @Value("${pws.database.derby.scheme}") private String derbyScheme;

    @Value("${pws.database.derby.mem.enable}") private boolean derbyMemDb;

    @Value("${pws.database.init.sql}") private String initSql;

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
}
