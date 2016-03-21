package cn.com.xinli.portal.web.rest;

import cn.com.xinli.portal.core.nas.Nas;
import cn.com.xinli.portal.web.auth.challenge.Challenge;
import cn.com.xinli.portal.web.auth.token.RestToken;

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
}
