package cn.com.xinli.portal.filter;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Abstract Rest filter.
 *
 * This filter extends from spring-security {@link OncePerRequestFilter}.
 * Project: xpws
 *
 * @author zhoupeng 2015/12/30.
 */
public abstract class AbstractRestFilter extends OncePerRequestFilter implements InitializingBean {
    /** Inclusive path array. */
    protected final List<String> filterPathMatches = new ArrayList<>();

    /**
     * Set filter path matches.
     * @param filterPathMatches filter path matches.
     */
    public final void setFilterPathMatches(Collection<String> filterPathMatches) {
        this.filterPathMatches.addAll(filterPathMatches);
    }

    /**
     * Check request requires to filter.
     * @param request request.
     * @return true if needs.
     */
    protected final boolean requiresFilter(HttpServletRequest request) {
        String uri = request.getRequestURI();
        return filterPathMatches.stream().anyMatch(uri::startsWith);
    }
}
