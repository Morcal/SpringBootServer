package cn.com.xinli.portal.rest;

import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.ExceptionTranslationFilter;

/**
 * Project: xpws
 *
 * @author zhoupeng 2015/12/19.
 */
public class RestExceptionTranslationFilter extends ExceptionTranslationFilter {
    public RestExceptionTranslationFilter(AuthenticationEntryPoint authenticationEntryPoint) {
        super(authenticationEntryPoint);
    }
}
