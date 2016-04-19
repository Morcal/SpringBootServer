package cn.com.xinli.portal.web.rest;

import cn.com.xinli.portal.core.activity.Activity;
import cn.com.xinli.portal.core.certificate.Certificate;
import cn.com.xinli.portal.core.configuration.ServerConfiguration;
import cn.com.xinli.portal.core.nas.Nas;
import cn.com.xinli.portal.core.runtime.LoadStatistics;
import cn.com.xinli.portal.core.runtime.NasStatistics;
import cn.com.xinli.portal.core.runtime.SessionStatistics;
import cn.com.xinli.portal.core.runtime.TotalSessionStatistics;
import cn.com.xinli.portal.core.session.Session;
import cn.com.xinli.portal.web.auth.challenge.Challenge;
import cn.com.xinli.portal.web.auth.token.RestToken;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Administration REST response builders.
 *
 * @author zhoupeng, created on 2016/3/21.
 */
public class AdminResponseBuilders {
    public static SuccessResponseBuilder successResponseBuilder() {
        return new SuccessResponseBuilder();
    }

    public static ChallengeResponseBuilder challengeResponseBuilder() {
        return new ChallengeResponseBuilder();
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

    public static CertificateResponseBuilder certificateResponseBuilder(Stream<Certificate> stream) {
        return new CertificateResponseBuilder(stream);
    }

    public static AppResponseBuilder appResponseBuilder() {
        return new AppResponseBuilder();
    }

    public static SystemStatisticsBuilder systemStatisticsBuilder() {
        return new SystemStatisticsBuilder();
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

    public static class SuccessResponseBuilder extends AdminResponseBuilder<RestResponse> {

        SuccessResponseBuilder() {
            super(false);
        }

        @Override
        protected RestResponse buildInternal() {
            return new RestResponse();
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
    public static class ChallengeResponseBuilder extends AdminResponseBuilder<RestResponse> {
        private Challenge challenge;
        private int challengeTtl;
        private RestToken token;

        ChallengeResponseBuilder() {
            super(false);
        }

        public ChallengeResponseBuilder setChallenge(Challenge challenge) {
            this.challenge = challenge;
            return this;
        }

        public ChallengeResponseBuilder setChallengeTtl(int challengeTtl) {
            this.challengeTtl = challengeTtl;
            return this;
        }

        public ChallengeResponseBuilder setToken(RestToken token) {
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

    /**
     * Certificate response builder.
     */
    public static class CertificateResponseBuilder extends AdminResponseBuilder<CertificateResponse> {
        private Stream<Certificate> stream;

        CertificateResponseBuilder(Stream<Certificate> stream) {
            super(false);
            this.stream = stream;
        }

        @Override
        protected CertificateResponse buildInternal() {
            CertificateResponse response = new CertificateResponse();
            response.setStream(stream);
            return response;
        }
    }

    /**
     * App response builder.
     */
    public static class AppResponseBuilder extends AdminResponseBuilder<AppResponse> {
        private String os;
        private String filepath;
        private boolean upToDate;
        private String version;

        AppResponseBuilder() {
            super(false);
        }

        public AppResponseBuilder setOs(String os) {
            this.os = os;
            return this;
        }

        public AppResponseBuilder setFilepath(String filepath) {
            this.filepath = filepath;
            return this;
        }

        public AppResponseBuilder setUpToDate(boolean upToDate) {
            this.upToDate = upToDate;
            return this;
        }

        public AppResponseBuilder setVersion(String version) {
            this.version = version;
            return this;
        }

        @Override
        protected AppResponse buildInternal() {
            AppResponse response = new AppResponse();
            AppResponse.App app = new AppResponse.App();
            app.setOs(os);
            app.setFilepath(filepath);
            app.setUpToDate(upToDate);
            app.setVersion(version);
            response.setApp(app);
            return response;
        }
    }

    /**
     * System statistics response builder.
     */
    public static class SystemStatisticsBuilder extends AdminResponseBuilder<SystemStatisticsResponse> {
        private LoadStatistics loadStatistics;
        private SessionStatistics sessionStatistics;
        private List<NasStatistics> nasStatistics;
        private TotalSessionStatistics totalSessionStatistics;

        private static final NasComparator comparator = new NasComparator();

        SystemStatisticsBuilder() {
            super(false);
        }

        public SystemStatisticsBuilder setLoad(LoadStatistics statistics) {
            loadStatistics = statistics;
            return this;
        }

        public SystemStatisticsBuilder setSession(SessionStatistics statistics) {
            sessionStatistics = statistics;
            return this;
        }

        public SystemStatisticsBuilder setDevices(List<NasStatistics> devices) {
            nasStatistics = devices;
            return this;
        }

        public SystemStatisticsBuilder setTotal(TotalSessionStatistics total) {
            totalSessionStatistics = total;
            return this;
        }

        @Override
        protected SystemStatisticsResponse buildInternal() {
            SystemStatisticsResponse response = new SystemStatisticsResponse();
            response.setLoadStatistics(loadStatistics);
            response.setSessionStatistics(sessionStatistics);
            response.setTotalSessionStatistics(totalSessionStatistics);
            response.setNasStatistics(nasStatistics);
            Collections.sort(nasStatistics, comparator);
            return response;
        }

        static class NasComparator implements Comparator<NasStatistics> {
            @Override
            public int compare(NasStatistics o1, NasStatistics o2) {
                if (o1 == o2)
                    return 0;

                Long diff = o1.getNasId() - o2.getNasId();
                return diff.intValue();
            }
        }
    }
}
