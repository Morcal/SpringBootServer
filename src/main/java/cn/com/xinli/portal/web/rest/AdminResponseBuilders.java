package cn.com.xinli.portal.web.rest;

import cn.com.xinli.portal.core.activity.Activity;
import cn.com.xinli.portal.core.configuration.ServerConfiguration;
import cn.com.xinli.portal.core.nas.Nas;
import cn.com.xinli.portal.core.session.Session;
import cn.com.xinli.portal.web.auth.challenge.Challenge;
import cn.com.xinli.portal.web.auth.token.RestToken;

import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Administration REST response builders.
 *
 * @author zhoupeng, created on 2016/3/21.
 */
public class AdminResponseBuilders {

    public static RestResponseBuilder restResponseBuilder() {
        return new RestResponseBuilder();
    }

    public static NasResponseBuilder nasResponseBuilder(Stream<Nas> stream) {
        return new NasResponseBuilder(stream);
    }

    public static ServerConfigurationResponseBuilder serverConfigurationResponseBuilder(
            ServerConfiguration configuration) {
        return new ServerConfigurationResponseBuilder(configuration);
    }

    public static SessionsResponseBuilder sessionsResponseBuilder() {
        return new SessionsResponseBuilder();
    }

    public static ActivityResponseBuilder activityResponseBuilder() {
        return new ActivityResponseBuilder();
    }

    /**
     * Abstract administration response builder.
     * @param <T>
     */
    public static abstract class AdminResponseBuilder<T> {
        /** If server truncated response. */
        private boolean truncated;

        /** Server time (UNIX epoch time) when response was created. */
        long createdAt;

        /**
         * Internal build target response.
         *
         * @return response.
         */
        protected abstract T buildInternal();

        AdminResponseBuilder(boolean truncated) {
            this.truncated = truncated;
            this.createdAt = System.currentTimeMillis() / 1000L;
        }

        public final T build() {
            T target = buildInternal();

            assert target != null;

            if (!(target instanceof RestResponse)) {
                return target;
            }

            RestResponse response = RestResponse.class.cast(target);
            response.setTruncated(truncated);
            response.setCreatedAt(createdAt);

            return target;
        }
    }

    /**
     * NAS response builder.
     */
    public static class NasResponseBuilder extends AdminResponseBuilder<NasResponse> {
        Stream<Nas> stream;
        NasResponseBuilder(Stream<Nas> stream) {
            super(false);
            this.stream = stream;
        }

        @Override
        protected NasResponse buildInternal() {
            NasResponse response = new NasResponse();
            response.setStream(stream);
            return response;
        }
    }

    /**
     * Rest response builder.
     */
    public static class RestResponseBuilder extends AdminResponseBuilder<RestResponse> {
        private Challenge challenge;
        private int challengeTtl;
        private RestToken token;

        RestResponseBuilder() {
            super(false);
        }

        public RestResponseBuilder setChallenge(Challenge challenge) {
            this.challenge = challenge;
            return this;
        }

        public RestResponseBuilder setChallengeTtl(int challengeTtl) {
            this.challengeTtl = challengeTtl;
            return this;
        }

        public RestResponseBuilder setToken(RestToken token) {
            this.token = token;
            return this;
        }

        @Override
        protected RestResponse buildInternal() {
            RestResponse response = new RestResponse();
            if (challenge != null) {
                RestResponseBuilders.AuthenticationBuilder builder =
                        RestResponseBuilders.authenticationBuilder(challenge);
                builder.setChallengeTtl(challengeTtl);
                response.setAuthentication(builder.build());
            }

            if (token != null) {
                response.setAuthorization(RestResponseBuilders.authorizationBuilder(token).build());
            }

            return response;
        }
    }

    /**
     * Server configuration response builder.
     */
    public static class ServerConfigurationResponseBuilder
            extends AdminResponseBuilder<ServerConfigurationResponse> {
        ServerConfiguration serverConfiguration;

        ServerConfigurationResponseBuilder(ServerConfiguration serverConfiguration) {
            super(false);
            this.serverConfiguration = serverConfiguration;
        }

        @Override
        protected ServerConfigurationResponse buildInternal() {
            ServerConfigurationResponse response = new ServerConfigurationResponse();
            response.setServerConfiguration(serverConfiguration);
            return response;
        }
    }

    /**
     * Sessions response builder.
     */
    public static class SessionsResponseBuilder extends AdminResponseBuilder<SessionsResponse> {
        Stream<Session> stream;
        long count;

        SessionsResponseBuilder() {
            super(false);
        }

        public SessionsResponseBuilder setStream(Stream<Session> stream) {
            this.stream = stream;
            return this;
        }

        public SessionsResponseBuilder setCount(long count) {
            this.count = count;
            return this;
        }

        @Override
        protected SessionsResponse buildInternal() {
            SessionsResponse response = new SessionsResponse();
            response.setCount(count);
            response.setSessions(stream.collect(Collectors.toList()));
            return response;
        }
    }

    /**
     * Activity response builder.
     */
    public static class ActivityResponseBuilder extends AdminResponseBuilder<ActivityResponse> {
        private Stream<Activity> stream;
        private long count;

        ActivityResponseBuilder() {
            super(false);
        }

        public ActivityResponseBuilder setStream(Stream<Activity> stream) {
            this.stream = stream;
            return this;
        }

        public ActivityResponseBuilder setCount(long count) {
            this.count = count;
            return this;
        }

        @Override
        protected ActivityResponse buildInternal() {
            ActivityResponse response = new ActivityResponse();
            response.setStream(stream);
            response.setCount(count);
            return response;
        }
    }
}
