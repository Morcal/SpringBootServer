package cn.com.xinli.portal.rest;

import cn.com.xinli.portal.PortalException;
import cn.com.xinli.portal.ServerConfig;
import cn.com.xinli.portal.Session;
import cn.com.xinli.portal.rest.auth.challenge.Challenge;
import cn.com.xinli.portal.rest.bean.Authentication;
import cn.com.xinli.portal.rest.bean.Authorization;
import cn.com.xinli.portal.rest.bean.Failure;
import cn.com.xinli.portal.rest.bean.RestBean;
import cn.com.xinli.portal.rest.configuration.CachingConfiguration;
import cn.com.xinli.portal.rest.token.RestAccessToken;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
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


    public static ErrorBuilder errorBuilder() {
        return new ErrorBuilder();
    }

    public static AuthenticationBuilder authenticationBuilder(Challenge challenge) {
        return new AuthenticationBuilder(challenge);
    }

    public static SessionBuilder sessionBuilder(Session session, Token token) {
        return new SessionBuilder(session, token);
    }

    public static AuthorizationBuilder authorizationBuilder(RestAccessToken token) {
        return new AuthorizationBuilder(token);
    }

    public static class ErrorBuilder implements Builder<Failure> {
        private String error;
        private String description;
        private String url;

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

        @Override
        public Failure build() {
            Failure failure = new Failure();
            failure.setError(StringUtils.defaultString(error, RestResponse.ERROR_UNKNOWN_ERROR));
            failure.setDescription(StringUtils.defaultString(description));
            failure.setUrl(StringUtils.defaultString(url));
            return failure;
        }
    }

    public static class SessionBuilder implements Builder<cn.com.xinli.portal.rest.bean.Session> {
        @Autowired
        private ServerConfig serverConfig;
        private final Session session;
        private final Token token;

        public SessionBuilder(Session session, Token token) {
            this.session = session;
            this.token = token;
        }

        @Override
        public cn.com.xinli.portal.rest.bean.Session build() {
            if (session == null) {
                throw new PortalException("Server failed to locate challenge.");
            } else {
                cn.com.xinli.portal.rest.bean.Session session = new cn.com.xinli.portal.rest.bean.Session();
                session.setKeepaliveInterval(serverConfig.getKeepaliveInterval());
                session.setKeepalive(serverConfig.requiresKeepalive());
                session.setId(String.valueOf(this.session.getId()));
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
                throw new PortalException("Server failed to locate authentication.");
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
        private final RestAccessToken token;

        public AuthorizationBuilder(RestAccessToken token) {
            this.token = token;
        }

        @Override
        public Authorization build() {
            if (token == null) {
                throw new PortalException("Server failed to locate authorization.");
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
