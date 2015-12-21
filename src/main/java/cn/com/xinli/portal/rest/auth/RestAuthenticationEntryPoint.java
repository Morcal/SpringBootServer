package cn.com.xinli.portal.rest.auth;

import cn.com.xinli.portal.rest.RestResponse;
import cn.com.xinli.portal.rest.RestResponseBuilders;
import cn.com.xinli.portal.rest.bean.RestBean;
import cn.com.xinli.portal.rest.token.InvalidAccessTokenException;
import cn.com.xinli.portal.rest.token.InvalidSessionTokenException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
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
        assert e != null;
        if (e instanceof InvalidAccessTokenException) {
            RestBean invalidCredentials = RestResponseBuilders.errorBuilder()
                    .setToken(((InvalidAccessTokenException) e).getToken())
                    .setError(RestResponse.ERROR_INVALID_CLIENT_GRANT).build();
            httpServletResponse.setStatus(HttpStatus.FORBIDDEN.value());
            httpServletResponse.getWriter().print(new ObjectMapper().writeValueAsString(invalidCredentials));
        } else if (e instanceof InvalidSessionTokenException) {
            RestBean invalidCredentials = RestResponseBuilders.errorBuilder()
                    .setToken(((InvalidSessionTokenException) e).getToken())
                    .setError(RestResponse.ERROR_INVALID_SESSION_GRANT)
                    .build();
            httpServletResponse.setStatus(HttpStatus.FORBIDDEN.value());
            httpServletResponse.getWriter().print(new ObjectMapper().writeValueAsString(invalidCredentials));
        } else {
            /* Server internal error. */
            RestBean internalError = RestResponseBuilders.errorBuilder().
                    setError(RestResponse.ERROR_SERVER_ERROR).build();
            httpServletResponse.setStatus(HttpStatus.OK.value());
            httpServletResponse.getWriter().print(new ObjectMapper().writeValueAsString(internalError));
        }
    }
}
