package cn.com.xinli.portal.filter;

import cn.com.xinli.portal.core.PortalError;
import cn.com.xinli.portal.support.rest.EntryPoint;
import cn.com.xinli.portal.support.rest.Provider;
import cn.com.xinli.portal.configuration.SecurityConfiguration;
import cn.com.xinli.portal.support.rest.RestResponse;
import cn.com.xinli.portal.support.rest.RestResponseBuilders;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

/**
 * Rate limiting filter.
 *
 * This filter implements PWS server rate limiting.
 *
 * Each requests from remote client will be saved in cache.
 * Each cache item has been set with time to idle to 1 second,
 * which means cached items will be expired in exactly 1 second.
 *
 * If any user try to request server at a rating over server
 * settings, cache item associated with his address may hit
 * multiply times and reaches limited rate set by server.
 * Server will deny that and subsequent requests, and limit
 * remote with a lesser rating (5 requests per 3 seconds).
 *
 * This class does not implement functionality described ahead.
 *
 * Project: xpws
 *
 * @author zhoupeng 2015/12/30.
 */
@Component
@Order(Integer.MIN_VALUE)
public class RateLimitingFilter extends AbstractRestFilter {
    /** Logger. */
    private final Logger logger = LoggerFactory.getLogger(RateLimitingFilter.class);

    /** Json factory. */
    private static final JsonFactory factory = new JsonFactory();

    /** Fallback error string. */
    private static final String RATE_LIMITING_REACHED_ERROR = "{\"error\": \"request_rate_limited\"}";

    @Autowired
    private Provider restApiProvider;

    @Autowired
    private Ehcache rateLimitingCache;

    @Override
    public void afterPropertiesSet() throws ServletException {
        super.afterPropertiesSet();
        List<List<String>> list = restApiProvider.getRegistrations().stream()
                .map(registration ->
                        registration.getApis().stream()
                                .map(EntryPoint::getUrl)
                                .collect(Collectors.toList()))
                .collect(Collectors.toList());

        Set<String> urls = new HashSet<>();
        list.forEach(strings -> strings.forEach(urls::add));

        if (logger.isDebugEnabled()) {
            urls.forEach(url -> logger.info("Adding rate-limiting filter path: {}.", url));
        }

        setFilterPathMatches(urls);
    }

    /**
     * Deny remote with error HTTP 403.
     * @param response response.
     */
    private void denyRemoteWithError(HttpServletResponse response) {
        RestResponse error = RestResponseBuilders.errorBuilder()
                .setError(PortalError.of("rest_request_rate_limited"))
                .setDescription("Request rate limiting reached.")
                .build();

        String errorText;
        try {
            errorText = new ObjectMapper(factory).writeValueAsString(error);
        } catch (JsonProcessingException e) {
            errorText = RATE_LIMITING_REACHED_ERROR;
        }

        try {
            response.setHeader("Content-Type", "application/json");
            response.setStatus(HttpStatus.FORBIDDEN.value());
            response.getWriter().write(errorText);
            response.getWriter().flush();
            response.getWriter().close();
        } catch (IOException e) {
            // Connection may be closed/reset by remote, nothing we can do, just log it.
            logger.warn("- send rate limiting error failed, {}", e.getMessage());
        }
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        if (requiresFilter(request)) {
            String realIp = request.getHeader("X-Real-Ip");
            String remote = StringUtils.isEmpty(realIp) ? request.getRemoteAddr() : realIp;

            /* Get from cache will update statistics, including hit count. */
            Element element = rateLimitingCache.get(remote);
            long now = System.currentTimeMillis();
            if (element != null) {
                AccessTimeTrack track = (AccessTimeTrack) element.getObjectValue();
                if (!track.trackAndCheckRate(now)) {
                    /* Rate limiting exceeded. */
                    logger.warn("! client from {} exceeded rate limiting.", remote);
                    rateLimitingCache.put(element);
                    denyRemoteWithError(response);
                    return; /* DO NOT pass to next filter in chain. */
                }
            } else {
                /* EhCache get/put operations are thread-safe. */
                AccessTimeTrack track = new AccessTimeTrack(SecurityConfiguration.RATE_LIMITING, 1L);
                track.trackAndCheckRate(now);
                element = new Element(remote, track, 1, 1);
                rateLimitingCache.put(element);
            }
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Records of REST API accesses.
     *
     * This implementation should be thread-safe.
     * Project: xpws
     *
     * @author zhoupeng 2015/12/31.
     */
    class AccessTimeTrack {
        /** Allowed access max counter. */
        private int allowed;

        /** Allowed access counter time range in milliseconds. */
        private long maxTimeDiff;

        /** Tracked records. */
        private final Queue<Long> accessTimes;

        public AccessTimeTrack(int allowed, long maxTimeDiff) {
            this.allowed = allowed;
            this.maxTimeDiff = maxTimeDiff * 1000L;
            this.accessTimes = new ConcurrentLinkedQueue<>();
        }

        /**
         * Track current access time and check if exceeds rate-limiting.
         * @param timestamp current access time.
         * @return true if access is allowed.
         */
        public boolean trackAndCheckRate(long timestamp) {
            accessTimes.offer(timestamp);

            if (accessTimes.size() > allowed) {
                /* To prevent attackers send flood to cause server run
                 * out of memory, keep track at maximum of 'allowed'.
                 */
                while (accessTimes.size() > allowed) {
                    accessTimes.poll();
                }
                return false;
            }

            Iterator<Long> iterator = accessTimes.iterator();
            while (iterator.hasNext()) {
                Long head = iterator.next();
                if ((timestamp - head) > maxTimeDiff) {
                    iterator.remove();
                } else {
                    /* Stop iteration. */
                    break;
                }
            }
            return true;
        }
    }
}
