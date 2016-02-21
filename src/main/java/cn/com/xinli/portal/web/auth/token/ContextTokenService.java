package cn.com.xinli.portal.web.auth.token;

import cn.com.xinli.portal.core.Context;
import cn.com.xinli.portal.core.session.SessionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    @Override
    protected TokenScope getTokenScope() {
        return TokenScope.PORTAL_CONTEXT_TOKEN_SCOPE;
    }

    @Override
    protected int getTokenTtl() {
        return Integer.MAX_VALUE;
    }

    @Override
    protected boolean verifyExtendedInformation(String extendedInformation) {
        Context context = Context.parse(extendedInformation);
        if (!context.isValid()) {
            return false;
        }

        try {
            return sessionService.exists(Long.valueOf(context.getSession()));
        } catch (NumberFormatException e) {
            if (logger.isDebugEnabled()) {
                logger.debug("* Invalid context token");
            }
            return false;
        }
    }
}
