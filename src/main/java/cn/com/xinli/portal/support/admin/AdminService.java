package cn.com.xinli.portal.support.admin;

import cn.com.xinli.portal.core.PortalError;
import cn.com.xinli.portal.core.PortalException;
import cn.com.xinli.portal.core.RemoteException;
import cn.com.xinli.portal.core.configuration.ServerConfiguration;
import cn.com.xinli.portal.core.configuration.ServerConfigurationService;
import cn.com.xinli.portal.util.Asserts;
import cn.com.xinli.portal.util.CodecUtils;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * System administration service.
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2016/3/14.
 */
@Service
public class AdminService {

    @Autowired
    private ServerConfigurationService serverConfigurationService;

    /**
     * Perform CHAP authentication.
     * @param challenge challenge.
     * @param username username.
     * @param password password.
     * @return full populated {@link AdminCredentials}.
     * @throws PortalException
     */
    private boolean verify(String challenge, String username, String password) throws PortalException {
        Asserts.notBlank(challenge);

        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
            return false;
        }

        final ServerConfiguration serverConfiguration = serverConfigurationService.getServerConfiguration();
        final String expectUsername = serverConfigurationService.getServerConfiguration().getDefaultAdminUsername(),
                expectPassword = serverConfigurationService.getServerConfiguration().getDefaultAdminPassword();

        /* Calculate CHAP password. */
        final String expectPasswordMd5 = new String(
                Hex.encodeHex(CodecUtils.md5sum((expectPassword + challenge).getBytes())));

        if (!StringUtils.isEmpty(expectUsername) && !StringUtils.isEmpty(expectPasswordMd5)) {
            if (StringUtils.equals(expectUsername, username) &&
                    StringUtils.equals(expectPasswordMd5, password)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Perform CHAP authentication.
     * @param credentials credentials.
     * @return full populated {@link AdminCredentials}.
     * @throws PortalException
     */
    public AdminCredentials verify(AdminCredentials credentials) throws PortalException {
        Objects.requireNonNull(credentials, AdminCredentials.EMPTY_CREDENTIALS);

        if (!verify(credentials.getChallenge(), credentials.getUsername(), credentials.getPassword())) {
            throw new RemoteException(PortalError.REST_LOGIN_REQUIRED);
        }

        /* Don't need to populate anything. */
        return credentials;
    }
}
