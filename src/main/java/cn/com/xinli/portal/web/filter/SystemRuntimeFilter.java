package cn.com.xinli.portal.web.filter;

import cn.com.xinli.portal.core.runtime.LoadStatistics;
import cn.com.xinli.portal.core.runtime.Runtime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * System runtime filter.
 * @author zhoupeng, created on 2016/4/7.
 */
@Component
@Order(Integer.MIN_VALUE)
public class SystemRuntimeFilter extends OncePerRequestFilter {

    @Autowired
    private Runtime runtime;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {
        long start = System.currentTimeMillis();
        filterChain.doFilter(request, response);

        if (!request.getRequestURI().contains("download")) {
            long responseTime = System.currentTimeMillis() - start;
            LoadStatistics.LoadRecord record = new LoadStatistics.LoadRecord(false);
            record.setResponseTime(responseTime);
            runtime.addLoadRecord(record);
        }
    }
}
