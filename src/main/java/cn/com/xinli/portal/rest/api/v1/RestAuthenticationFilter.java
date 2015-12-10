package cn.com.xinli.portal.rest.api.v1;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;

import javax.servlet.http.HttpServletRequest;

/**
 * Project: portal
 *
 * @author zhoupeng 2015/12/10.
 */
public class RestAuthenticationFilter extends AbstractPreAuthenticatedProcessingFilter {

    @Autowired
    private AuthenticationManager authenticationManager;

    public RestAuthenticationFilter() {
        setContinueFilterChainOnUnsuccessfulAuthentication(false);
    }

    @Override
    protected Object getPreAuthenticatedPrincipal(HttpServletRequest httpServletRequest) {
        String hv = httpServletRequest.getHeader(HttpDigestCredentials.HEADER_NAME);
        HttpDigestCredentials digest = HttpDigestCredentials.of(hv.trim());
        return digest.getParameter(HttpDigestCredentials.CLIENT_ID);
    }

    @Override
    protected Object getPreAuthenticatedCredentials(HttpServletRequest httpServletRequest) {
        String hv = httpServletRequest.getHeader(HttpDigestCredentials.HEADER_NAME);
        return HttpDigestCredentials.of(hv.trim());
    }
}
