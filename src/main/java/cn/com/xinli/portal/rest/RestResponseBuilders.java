package cn.com.xinli.portal.rest;

import cn.com.xinli.portal.ServerConfig;
import cn.com.xinli.portal.Session;
import cn.com.xinli.portal.rest.auth.AccessAuthentication;
import cn.com.xinli.portal.rest.auth.HttpDigestCredentials;
import cn.com.xinli.portal.rest.auth.challenge.Challenge;
import cn.com.xinli.portal.rest.bean.*;
import cn.com.xinli.portal.rest.configuration.CachingConfiguration;
import cn.com.xinli.portal.rest.token.AccessToken;
import org.apache.catalina.Server;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.token.Token;

/**
 * Project: portal
 *
 * @author zhoupeng 2015/12/8.
 */
public class RestResponseBuilders {

    interface Builder<T extends RestBean> {
        T build();
    }

    public static SuccessBuilder successBuilder() {
        return new SuccessBuilder();
    }

    public static ErrorBuilder errorBuilder() {
        return new ErrorBuilder();
    }

    public static AuthenticationBuilder authenticationBuilder(Challenge challenge) {
        return new AuthenticationBuilder(challenge);
    }

    public static SessionBuilder sessionBuilder(Session session, Token token, ServerConfig serverConfig) {
        return new SessionBuilder(session, token, serverConfig);
    }

    public static AuthorizationBuilder authorizationBuilder(AccessToken token) {
        return new AuthorizationBuilder(token);
    }

    public static class SuccessBuilder implements Builder<Success> {
        private Session session = null;
        private Challenge challenge = null;
        private AccessAuthentication accessAuthentication = null;
        private boolean grantToken = false;
        private ServerConfig serverConfig;

        public SuccessBuilder setAccessAuthentication(AccessAuthentication accessAuthentication) {
            this.accessAuthentication = accessAuthentication;
            return this;
        }

        public SuccessBuilder setSession(Session session) {
            this.session = session;
            return this;
        }

        public SuccessBuilder setChallenge(Challenge challenge) {
            this.challenge = challenge;
            return this;
        }

        public SuccessBuilder setServerConfig(ServerConfig serverConfig) {
            this.serverConfig = serverConfig;
            return this;
        }

        public SuccessBuilder setGrantToken(boolean grantToken) {
            this.grantToken = grantToken;
            return this;
        }

        @Override
        public Success build() {
            Success success = new Success();
            /* Build session with/without session token. */
            if (session == null) {
                success.setSession(null);
            } else if (accessAuthentication == null || !grantToken) {
                success.setSession(sessionBuilder(session, null, serverConfig).build());
            } else {
                success.setSession(sessionBuilder(session, accessAuthentication.getSessionToken(), serverConfig).build());
            }

            /* Build authorization if challenge response in credentials. */
            if (accessAuthentication != null) {
                HttpDigestCredentials credentials = accessAuthentication.getCredentials();
                if (HttpDigestCredentials.containsChallenge(credentials)) {
                    /* Set authorization only when response to challenge. */
                    success.setAuthorization(authorizationBuilder(accessAuthentication.getAccessToken()).build());
                }
            } else {
                success.setAuthorization(null);
            }

            /* Build authentication if present. */
            success.setAuthentication(challenge == null ? null : authenticationBuilder(challenge).build());
            return success;
        }
    }

    public static class ErrorBuilder implements Builder<Failure> {
        private String error;
        private String description;
        private String url;
        private String token;

        public ErrorBuilder setError(String error) {
            this.error = error;
            return this;
        }

        public ErrorBuilder setDescription(String description) {
            this.description = description;
            return this;
        }

        public ErrorBuilder setUrl(String url) {
            this.url = url;
            return this;
        }

        public ErrorBuilder setToken(String token) {
            this.token = token;
            return this;
        }

        @Override
        public Failure build() {
            Failure failure = new Failure();
            failure.setError(StringUtils.defaultString(error, RestResponse.ERROR_UNKNOWN_ERROR));
            failure.setDescription(StringUtils.defaultString(description));
            failure.setUrl(StringUtils.defaultString(url));
            failure.setToken(StringUtils.defaultString(token));
            return failure;
        }
    }

    public static class SessionBuilder implements Builder<cn.com.xinli.portal.rest.bean.Session> {
        private final Session session;
        private final Token token;
        private final ServerConfig serverConfig;

        public SessionBuilder(Session session, Token token, ServerConfig serverConfig) {
            this.session = session;
            this.token = token;
            this.serverConfig = serverConfig;
        }

        @Override
        public cn.com.xinli.portal.rest.bean.Session build() {
            if (session == null) {
                return null;
            } else {
                cn.com.xinli.portal.rest.bean.Session session = new cn.com.xinli.portal.rest.bean.Session();
                session.setId(String.valueOf(this.session.getId()));
                session.setStarttime(this.session.getStartTime().getTime() / 1000L);
                if (serverConfig != null) {
                    session.setKeepaliveInterval(serverConfig.getKeepaliveInterval());
                    session.setKeepalive(serverConfig.requiresKeepalive());
                }
                if (token != null) {
                    session.setToken(token.getKey());
                    session.setTokenExpiresIn(CachingConfiguration.SESSION_TOKEN_TTL);
                }
                return session;
            }
        }
    }

    public static class AuthenticationBuilder implements Builder<Authentication> {
        private final Challenge challenge;

        public AuthenticationBuilder(Challenge challenge) {
            this.challenge = challenge;
        }

        @Override
        public Authentication build() {
            if (challenge == null) {
                throw new IllegalStateException("Server failed to locate challenge.");
            } else {
                Authentication authentication = new Authentication();
                authentication.setExpiresIn(CachingConfiguration.CHALLENGE_TTL);
                authentication.setChallenge(challenge.getChallenge());
                authentication.setNonce(challenge.getNonce());
                return authentication;
            }
        }
    }

    public static class AuthorizationBuilder implements Builder<Authorization> {
        private final AccessToken token;

        public AuthorizationBuilder(AccessToken token) {
            this.token = token;
        }

        @Override
        public Authorization build() {
            if (token == null) {
                throw new IllegalStateException("Server failed to locate authorization.");
            } else {
                Authorization authorization = new Authorization();
                authorization.setToken(token.getKey());
                authorization.setExpiresIn(CachingConfiguration.ACCESS_TOKEN_TTL);
                authorization.setScope(token.getScope());
                authorization.setRefreshToken(""); /* Refresh token not supported yet. */
                return authorization;
            }
        }
    }
}
