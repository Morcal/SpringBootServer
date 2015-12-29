package cn.com.xinli.portal.rest.configuration;

import cn.com.xinli.portal.auth.AuthorizationServer;
import cn.com.xinli.portal.rest.auth.RestAuthorizationServer;
import cn.com.xinli.portal.rest.auth.challenge.ChallengeManager;
import cn.com.xinli.portal.rest.auth.challenge.ChallengeService;
import cn.com.xinli.portal.rest.auth.challenge.EhCacheChallengeManager;
import cn.com.xinli.portal.rest.token.AccessTokenService;
import cn.com.xinli.portal.rest.token.SessionTokenService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * PWS REST modules configurations.
 *
 * Project: xpws
 *
 * @author zhoupeng 2015/12/19.
 */
@Configuration
public class RestConfiguration {
    @Bean
    public AuthorizationServer authorizationServer() {
        return new RestAuthorizationServer();
    }

    @Bean
    public ChallengeManager challengeManager() {
        return new EhCacheChallengeManager();
    }

    @Bean
    public ChallengeService challengeService() {
        return (ChallengeService) challengeManager();
    }

    @Bean(name = "rest-token-service")
    public AccessTokenService restTokenService() {
        return new AccessTokenService();
    }

    @Bean(name = "session-token-service")
    public SessionTokenService sessionTokenService() {
        return new SessionTokenService();
    }
}
