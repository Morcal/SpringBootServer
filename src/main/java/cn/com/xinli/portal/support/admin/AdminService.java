package cn.com.xinli.portal.support.admin;

import cn.com.xinli.portal.core.PortalError;
import cn.com.xinli.portal.core.PortalException;
import cn.com.xinli.portal.core.RemoteException;
import cn.com.xinli.portal.core.configuration.ServerConfiguration;
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
    private ServerConfiguration serverConfiguration;

    /**
     * Perform CHAP authentication.
     * @param challenge challenge.
     * @param credentials credentials.
     * @return full populated {@link AdminCredentials}.
     * @throws PortalException
     */
    public AdminCredentials verify(String challenge, AdminCredentials credentials) throws PortalException {
        Objects.requireNonNull(credentials, AdminCredentials.EMPTY_CREDENTIALS);
        Asserts.notBlank(challenge);

        final String username = credentials.getUsername(),
                password = credentials.getPassword();
        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
            throw new RemoteException(PortalError.REST_LOGIN_FAILED);
        }

        final String expectUsername = serverConfiguration.getDefaultAdminUsername(),
                expectPassword = serverConfiguration.getDefaultAdminPassword();

        /* Calculate CHAP password. */
        final String expectPasswordMd5 = new String(
                Hex.encodeHex(CodecUtils.md5sum((expectPassword + challenge).getBytes())));

        if (!StringUtils.isEmpty(expectUsername) && !StringUtils.isEmpty(expectPasswordMd5)) {
            if (StringUtils.equals(expectUsername, username) &&
                    StringUtils.equals(expectPasswordMd5, password)) {
                return credentials;
            }
        }

        throw new RemoteException(PortalError.REST_LOGIN_FAILED);
    }

    /**
     * Perform CHAP authentication.
     * @param credentials credentials.
     * @return full populated {@link AdminCredentials}.
     * @throws PortalException
     */
    public AdminCredentials verify(AdminCredentials credentials) throws PortalException {
        Objects.requireNonNull(credentials, AdminCredentials.EMPTY_CREDENTIALS);

        final String username = credentials.getUsername(),
                password = credentials.getPassword();
        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
            throw new RemoteException(PortalError.REST_LOGIN_REQUIRED);
        }

        final String expectUsername = serverConfiguration.getDefaultAdminUsername(),
                expectPassword = serverConfiguration.getDefaultAdminPassword();

        if (!StringUtils.isEmpty(expectUsername) && !StringUtils.isEmpty(expectPassword)) {
            if (StringUtils.equals(expectUsername, username) &&
                    StringUtils.equals(expectPassword, password)) {
                return credentials;
            }
        }

        throw new RemoteException(PortalError.REST_LOGIN_REQUIRED);
    }
}
