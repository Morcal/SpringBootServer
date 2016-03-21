package cn.com.xinli.portal.web.util;

import cn.com.xinli.portal.web.auth.HttpDigestCredentials;
import org.springframework.security.authentication.BadCredentialsException;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

/**
 * Credentials utility.
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2015/12/16.
 */
public class CredentialsUtils {
    /**
     * Get authentication string from http request header.
     * @param request http request.
     * @return optional.
     */
    private static Optional<String> getAuthenticationFromRequestHeader(HttpServletRequest request) {
        String authentication = request.getHeader(HttpDigestCredentials.HEADER_NAME);
        return Optional.ofNullable(authentication);
    }

    /**
     * Get credentials from request.
     * @param request request.
     * @return credential.
     * @throws BadCredentialsException if request does not contains well-formed credentials.
     */
    public static Optional<HttpDigestCredentials> getCredentials(HttpServletRequest request) {
        Optional<String> o = getAuthenticationFromRequestHeader(request);

        if (!o.isPresent()) {
            return Optional.empty();
        }

        try {
            final HttpDigestCredentials credentials = HttpDigestCredentials.of(o.get().trim());
            return Optional.ofNullable(credentials);
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
