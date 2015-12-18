package cn.com.xinli.portal.configuration;

import cn.com.xinli.portal.Nas;
import cn.com.xinli.portal.NasMapping;
import cn.com.xinli.portal.ServerConfig;
import cn.com.xinli.portal.SessionService;
import cn.com.xinli.portal.auth.AuthorizationServer;
import cn.com.xinli.portal.rest.RestSessionService;
import cn.com.xinli.portal.rest.auth.challenge.ChallengeManager;
import cn.com.xinli.portal.rest.auth.challenge.ChallengeService;
import cn.com.xinli.portal.rest.auth.RestAuthorizationServer;
import cn.com.xinli.portal.rest.auth.challenge.EhCacheChallengeManager;
import cn.com.xinli.portal.rest.token.RestAccessTokenService;
import cn.com.xinli.portal.rest.token.RestSessionTokenService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.security.core.token.TokenService;

/**
 * Project: portal
 *
 * @author zhoupeng 2015/12/6.
 */
@Configuration
@ImportResource("classpath:nas.xml")
public class PortalConfiguration {

    @Value("${application}") private String application;

    @Value("${private_key}") private String privateKey;

    @Value("${session.requiresKeepalive}") private boolean keepalive;

    @Value("${session.requiresKeepalive.interval}") private int keepaliveInterval;

    @Value("${database.derby.scheme}") private String derbyScheme;

    @Value("${database.derby.mem.enable}") private boolean derbyMemDb;

    @Value("${database.init.sql}") private String initSql;

    @Value("${rest.api.uri}") private String restApiUri;

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
        config.setReestApiUri(restApiUri);
        return config;
    }

    @Bean
    public ChallengeManager challengeManager() {
        return new EhCacheChallengeManager();
    }

    @Bean
    public ChallengeService challengeService() {
        return (ChallengeService) challengeManager();
    }

    @Bean
    public SessionService restSessionService() {
        return new RestSessionService();
    }

    @Bean
    public TokenService accessTokenService() {
        return new RestAccessTokenService();
    }

    @Bean
    public TokenService sessionTokenService() {
        return new RestSessionTokenService();
    }

    @Bean
    public AuthorizationServer authorizationServer() {
        return new RestAuthorizationServer();
    }

    @Bean
    public NasMapping deviceMapping() {
        return new NasMapping();
    }

}
