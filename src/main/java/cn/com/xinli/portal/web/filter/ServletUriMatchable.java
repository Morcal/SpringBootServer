package cn.com.xinli.portal.web.filter;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;

/**
 * HTTP path matchable.
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2016/2/4.
 */
public interface ServletUriMatchable {
    /**
     * Check request requires to filter.
     * @param request request.
     * @return true if needs.
     */
    boolean matches(HttpServletRequest request);
    /**
     * Set filter path matches.
     * @param matchedUris filter path matches.
     */
    void setMatchedUris(Collection<String> matchedUris);
}
