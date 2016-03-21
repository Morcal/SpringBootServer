package cn.com.xinli.portal.web.auth.token;

import cn.com.xinli.portal.core.PortalError;
import cn.com.xinli.portal.core.PortalException;
import cn.com.xinli.portal.core.RemoteException;
import cn.com.xinli.portal.core.Serializer;
import cn.com.xinli.portal.support.admin.AdminCredentials;
import cn.com.xinli.portal.support.admin.AdminService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.serializer.SerializationException;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * Portal context token service.
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2016/2/21.
 */
@Service
public class AdminTokenService extends AbstractTokenService {
    /** Logger. */
    private final Logger logger = LoggerFactory.getLogger(AdminTokenService.class);

    @Override
    protected TokenScope getTokenScope() {
        return TokenScope.SYSTEM_ADMIN_TOKEN_SCOPE;
    }

    @Override
    protected Serializer<TokenKey> getTokenKeySerializer() {
        return jsonTokenKeySerializer;
    }

    @Autowired
    private Serializer<TokenKey> jsonTokenKeySerializer;

    @Autowired
    private Serializer<AdminCredentials> adminCredentialsSerializer;

    @Autowired
    private AdminService adminService;

    @Override
    protected int getTokenTtl() {
        return Integer.MAX_VALUE;
    }

    /**
     * Decode a string to a context.
     * @param value context string.
     * @return context.
     */
    public AdminCredentials decode(String value) throws RemoteException {
        if (StringUtils.isEmpty(value)) {
            throw new RemoteException(PortalError.INVALID_REQUEST, "context value can not be blank.");
        }

        try {
            return adminCredentialsSerializer.deserialize(value.getBytes());
        } catch (SerializationException e) {
            throw new RemoteException(PortalError.INVALID_REQUEST, "invalid admin value.");
        }
    }

    /**
     * Encode context as a string.
     * @param credentials credentials.
     * @return JSON string.
     */
    public String encode(AdminCredentials credentials) throws RemoteException {
        Objects.requireNonNull(credentials);
        try {
            return new String(adminCredentialsSerializer.serialize(credentials));
        } catch (SerializationException e) {
            throw new RemoteException(PortalError.INVALID_REQUEST, "invalid admin");
        }
    }

    @Override
    protected boolean verifyExtendedInformation(String extendedInformation) {
        AdminCredentials credentials;
        try {
            credentials = decode(extendedInformation);

            return adminService.verify(credentials) != null;
        } catch (NumberFormatException | PortalException e) {
            if (logger.isDebugEnabled()) {
                logger.debug("* Invalid admin token");
            }
            return false;
        }
    }
}
