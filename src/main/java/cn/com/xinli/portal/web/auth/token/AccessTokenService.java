package cn.com.xinli.portal.web.auth.token;

import cn.com.xinli.portal.core.certificate.CertificateService;
import cn.com.xinli.portal.web.configuration.SecurityConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * REST Access token service.
 *
 * Project: xpws
 *
 * @author zhoupeng 2015/12/13.
 */
@Service
public class AccessTokenService extends AbstractTokenService {

    @Autowired
    private CertificateService certificateService;

    @Override
    protected TokenScope getTokenScope() {
        return TokenScope.PORTAL_ACCESS_TOKEN_SCOPE;
    }

    @Override
    protected boolean verifyExtendedInformation(String extendedInformation) {
        return certificateService.isCertified(extendedInformation);
    }

    @Override
    protected int getTokenTtl() {
        return SecurityConfiguration.ACCESS_TOKEN_TTL;
    }
}
