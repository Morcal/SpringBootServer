package cn.com.xinli.portal.rest.auth;

import cn.com.xinli.portal.rest.RestResponse;
import cn.com.xinli.portal.rest.RestResponseBuilders;
import cn.com.xinli.portal.rest.bean.RestBean;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Project: xpws
 *
 * @author zhoupeng 2015/12/15.
 */
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest httpServletRequest,
                         HttpServletResponse httpServletResponse,
                         AuthenticationException e) throws IOException, ServletException {
        if (e != null) {
            if (e instanceof BadCredentialsException) {
                /* Bad credentials. */
                RestBean invalidCredentials = RestResponseBuilders.errorBuilder().
                        setError(RestResponse.ERROR_INVALID_CREDENTIAL).build();
                httpServletResponse.sendError(HttpStatus.OK.value(),
                        new ObjectMapper().writeValueAsString(invalidCredentials));
            } else {
                /* Server internal error. */
                RestBean internalError = RestResponseBuilders.errorBuilder().
                        setError(RestResponse.ERROR_SERVER_ERROR).build();
                httpServletResponse.sendError(HttpStatus.OK.value(),
                        new ObjectMapper().writeValueAsString(internalError));
            }
        } else {
            /* Unauthorized request. */
            RestBean unauthorized = RestResponseBuilders.errorBuilder().
                    setError(RestResponse.ERROR_UNAUTHORIZED_REQUEST).build();
            httpServletResponse.sendError(HttpStatus.FORBIDDEN.value(),
                    new ObjectMapper().writeValueAsString(unauthorized));
        }
    }
}
