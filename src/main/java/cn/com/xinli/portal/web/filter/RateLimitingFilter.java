package cn.com.xinli.portal.web.filter;

import cn.com.xinli.portal.core.PortalError;
import cn.com.xinli.portal.core.ratelimiting.AccessTimeTrack;
import cn.com.xinli.portal.core.ratelimiting.TrackStore;
import cn.com.xinli.portal.web.rest.RestResponse;
import cn.com.xinli.portal.web.rest.RestResponseBuilders;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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

/**
 * Rate limiting filter.
 *
 * <p>This filter implements PWS server rate limiting.
 *
 * <p>Each requests from remote client will be saved in cache.
 * Each cache item has been set with time to idle to 1 second,
 * which means cached items will be expired in exactly 1 second.
 *
 * <p>If any user try to request server at a rating over server
 * settings, cache item associated with his address may hit
 * multiply times and reaches limited rate set by server.
 * Server will deny that and subsequent requests, and limit
 * remote with a lesser rating (5 requests per 3 seconds).
 *
 * <p>This class does not implement functionality described ahead.
 *
 * <p>Project: xpws
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
    private static final String RATE_LIMITING_REACHED_ERROR =
            "{\"error\": 151, \"description\":\"request_rate_limited\"}";

    @Autowired
    private TrackStore trackStore;

    @Override
    public void afterPropertiesSet() throws ServletException {
        super.afterPropertiesSet();
    }

    /**
     * Deny remote with error HTTP 403.
     *
     * @param response response.
     */
    private void denyRemoteWithError(HttpServletResponse response) {
        RestResponse error = RestResponseBuilders.errorBuilder()
                .setError(PortalError.REST_REQUEST_RATE_LIMITED)
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
        if (matches(request)) {
            String realIp = request.getHeader("X-Real-Ip");
            String remote = StringUtils.isEmpty(realIp) ? request.getRemoteAddr() : realIp;

            /* Get from cache will update statistics, including hit count. */
            AccessTimeTrack track = trackStore.get(remote);

            long now = System.currentTimeMillis();
            if (track != null) {
                if (!track.trackAndCheckRate(now)) {
                    /* Rate limiting exceeded. */
                    logger.info("! client from {} exceeded rate limiting.", remote);
                    trackStore.put(remote, track);
                    denyRemoteWithError(response);
                    return; /* DO NOT pass to next filter in chain. */
                }
            } else {
                trackStore.put(remote);
            }
        }

        filterChain.doFilter(request, response);
    }
}
