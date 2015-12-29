package cn.com.xinli.portal.rest;

import cn.com.xinli.portal.rest.bean.RestBean;
import cn.com.xinli.portal.rest.configuration.SecurityConfiguration;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
 * Project: xpws
 *
 * @author zhoupeng 2015/12/30.
 */
@Component
public class RateLimitingFilter extends AbstractRestFilter {
    /** Logger. */
    private final Logger logger = LoggerFactory.getLogger(RateLimitingFilter.class);

    /** Json factory. */
    private static final JsonFactory factory = new JsonFactory();

    /** Fallback error string. */
    private static final String RATE_LIMITING_REACHED_ERROR = "{\"error\": \"request_rate_limited\"}";

    @Autowired
    private Ehcache rateLimitingCache;

    /**
     * Deny remote with error HTTP 403.
     * @param response response.
     */
    private void denyRemoteWithError(HttpServletResponse response) {
        RestBean error = RestResponseBuilders.errorBuilder()
                .setError(RestResponse.ERROR_REQUEST_RATE_LIMITED)
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
            response.sendError(HttpStatus.FORBIDDEN.value(), errorText);
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
                long hitCount = element.getHitCount();
                if (hitCount >= SecurityConfiguration.RATE_LIMITING) {
                    /* Rate limiting exceeded. */
                    logger.warn("! client from {} exceeded rate limiting.", remote);
                    /* We create a new element with longer tti so that
                     * in next few seconds, only a few request form that remote
                     * may be accepted. If remote reached limiting rate,
                     * remote will be limited at a lesser rating (4~5 requests per 3 seconds).
                     * The limiting rate will drop back to normal only if
                     * there's no more requests in 3 seconds.
                     */
                    element = new Element(remote, remote, 3L, 3L, now, now, 1);
                    rateLimitingCache.put(element);
                    denyRemoteWithError(response);
                    /* DO NOT pass to next filter in chain. */
                    return;
                }
            } else {
                /* EhCache get/put operations are thread-safe. */
                element = new Element(remote, remote, 1L, 1L, now, now, 1L);
                rateLimitingCache.put(element);
            }
        }

        filterChain.doFilter(request, response);
    }
}
