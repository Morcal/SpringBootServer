package cn.com.xinli.portal.web.auth.token;

import cn.com.xinli.portal.core.Context;
import cn.com.xinli.portal.core.PortalError;
import cn.com.xinli.portal.core.RemoteException;
import cn.com.xinli.portal.core.Serializer;
import cn.com.xinli.portal.core.session.SessionService;
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
public class ContextTokenService extends AbstractTokenService {
    /** Logger. */
    private final Logger logger = LoggerFactory.getLogger(ContextTokenService.class);

    @Autowired
    private SessionService sessionService;

    @Autowired
    private Serializer<Context> contextSerializer;

    @Autowired
    private Serializer<TokenKey> jsonTokenKeySerializer;

    @Override
    protected TokenScope getTokenScope() {
        return TokenScope.PORTAL_CONTEXT_TOKEN_SCOPE;
    }

    @Override
    protected Serializer<TokenKey> getTokenKeySerializer() {
        return jsonTokenKeySerializer;
    }

    @Override
    protected int getTokenTtl() {
        return Integer.MAX_VALUE;
    }

    /**
     * Decode a string to a context.
     * @param value context string.
     * @return context.
     */
    public Context decode(String value) throws RemoteException {
        if (StringUtils.isEmpty(value)) {
            throw new RemoteException(PortalError.INVALID_REQUEST, "context value can not be blank.");
        }

        try {
            return contextSerializer.deserialize(value.getBytes());
        } catch (SerializationException e) {
            throw new RemoteException(PortalError.INVALID_REQUEST, "invalid context value.");
        }
    }

    /**
     * Encode context as a string.
     * @param context context.
     * @return JSON string.
     */
    public String encode(Context context) throws RemoteException {
        Objects.requireNonNull(context);
        try {
            return new String(contextSerializer.serialize(context));
        } catch (SerializationException e) {
            throw new RemoteException(PortalError.INVALID_REQUEST, "invalid context");
        }
    }

    @Override
    protected boolean verifyExtendedInformation(String extendedInformation) {
        Context context;
        try {
            context = decode(extendedInformation);

            return context.isValid() &&
                    sessionService.exists(Long.valueOf(context.getSession()));
        } catch (NumberFormatException | RemoteException e) {
            if (logger.isDebugEnabled()) {
                logger.debug("* Invalid context token");
            }
            return false;
        }
    }
}
