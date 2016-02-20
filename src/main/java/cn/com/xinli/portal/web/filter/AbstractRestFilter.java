package cn.com.xinli.portal.web.filter;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Abstract Rest filter.
 *
 * <p>This filter extends from spring-security {@link OncePerRequestFilter}.
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2015/12/30.
 */
public abstract class AbstractRestFilter extends OncePerRequestFilter
        implements ServletUriMatchable, InitializingBean {
    /** Inclusive path array. */
    private final List<String> matchedUris = new ArrayList<>();

    /**
     * Set filter path matches.
     * @param matchedUris filter path matches.
     */
    @Override
    public final void setMatchedUris(Collection<String> matchedUris) {
        this.matchedUris.addAll(matchedUris);
    }

    /**
     * Check request requires to filter.
     * @param request request.
     * @return true if needs.
     */
    @Override
    public final boolean matches(HttpServletRequest request) {
        return matchedUris.stream().anyMatch(request.getRequestURI()::startsWith);
    }
}
