package cn.com.xinli.portal.rest.auth;

import cn.com.xinli.rest.RestResponse;
import cn.com.xinli.portal.rest.RestResponseBuilders;
import cn.com.xinli.rest.RestResponse;
import cn.com.xinli.portal.rest.token.InvalidAccessTokenException;
import cn.com.xinli.portal.rest.token.InvalidSessionTokenException;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Rest authentication entry point.
 *
 * <p>
 * When rest authentication fails, PWS provides an entry point
 * to send authentication associated message back to clients.
 * <p>
 * Project: xpws
 *
 * @author zhoupeng 2015/12/15.
 */
@Component
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {
    /** Json factory. */
    private static final JsonFactory factory = new JsonFactory();

    @Override
    public void commence(HttpServletRequest httpServletRequest,
                         HttpServletResponse httpServletResponse,
                         AuthenticationException e) throws IOException, ServletException {
        assert e != null;
        if (e instanceof InvalidAccessTokenException) {
            RestResponse invalidCredentials = RestResponseBuilders.errorBuilder()
                    .setToken(((InvalidAccessTokenException) e).getToken())
                    .setError(RestResponse.ERROR_INVALID_CLIENT_GRANT)
                    .build();
            httpServletResponse.setStatus(HttpStatus.FORBIDDEN.value());
            httpServletResponse.getWriter().print(
                    new ObjectMapper(factory).writeValueAsString(invalidCredentials));
        } else if (e instanceof InvalidSessionTokenException) {
            RestResponse invalidCredentials = RestResponseBuilders.errorBuilder()
                    .setToken(((InvalidSessionTokenException) e).getToken())
                    .setError(RestResponse.ERROR_INVALID_SESSION_GRANT)
                    .build();
            httpServletResponse.setStatus(HttpStatus.FORBIDDEN.value());
            httpServletResponse.getWriter().print(
                    new ObjectMapper(factory).writeValueAsString(invalidCredentials));
        } else if (e instanceof BadCredentialsException) {
            RestResponse invalidCredentials = RestResponseBuilders.errorBuilder()
                    .setError(RestResponse.ERROR_INVALID_REQUEST)
                    .setDescription(e.getMessage())
                    .build();
            httpServletResponse.setStatus(HttpStatus.FORBIDDEN.value());
            httpServletResponse.getWriter().print(
                    new ObjectMapper(factory).writeValueAsString(invalidCredentials));
        } else {
            /* Server internal error. */
            RestResponse internalError = RestResponseBuilders.errorBuilder()
                    .setError(RestResponse.ERROR_SERVER_ERROR)
                    .setDescription(e.getMessage())
                    .build();
            httpServletResponse.setStatus(HttpStatus.OK.value());
            httpServletResponse.getWriter().print(
                    new ObjectMapper(factory).writeValueAsString(internalError));
        }
    }
}
