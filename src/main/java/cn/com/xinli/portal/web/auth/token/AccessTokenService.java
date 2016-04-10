package cn.com.xinli.portal.web.auth.token;

import cn.com.xinli.portal.core.Serializer;
import cn.com.xinli.portal.core.certificate.CertificateService;
import cn.com.xinli.portal.core.configuration.ServerConfigurationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * REST Access token service.
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2015/12/13.
 */
@Service
public class AccessTokenService extends AbstractTokenService {

    @Autowired
    private CertificateService certificateService;

    @Autowired
    private ServerConfigurationService serverConfigurationService;

    @Autowired
    private Serializer<TokenKey> delimiterTokenKeySerializer;

    @Override
    protected TokenScope getTokenScope() {
        return TokenScope.PORTAL_ACCESS_TOKEN_SCOPE;
    }

    @Override
    protected Serializer<TokenKey> getTokenKeySerializer() {
        return delimiterTokenKeySerializer;
    }

    @Override
    protected boolean verifyExtendedInformation(String extendedInformation) {
        return certificateService.isCertified(extendedInformation);
    }

    @Override
    protected int getTokenTtl() {
        return serverConfigurationService.getServerConfiguration().getRestConfiguration().getTokenTtl();
    }
}
