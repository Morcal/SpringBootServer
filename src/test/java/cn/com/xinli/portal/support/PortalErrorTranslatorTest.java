package cn.com.xinli.portal.support;

import cn.com.xinli.portal.configuration.ErrorConfiguration;
import cn.com.xinli.portal.core.PlatformException;
import cn.com.xinli.portal.core.PortalError;
import cn.com.xinli.portal.core.ServerException;
import cn.com.xinli.portal.transport.AuthenticationException;
import cn.com.xinli.portal.transport.PortalProtocolException;
import cn.com.xinli.portal.transport.ProtocolError;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;

/**
 * Project: xpws
 *
 * @author zhoupeng 2016/1/20.
 */
public class PortalErrorTranslatorTest {
    final PortalErrorTranslator translator = new PortalErrorTranslator();

    @Before
    public void setup() {
        translator.setMessageTable(ErrorConfiguration.MESSAGE_TRANSLATE_TABLE);
        translator.setHttpStatusTable(ErrorConfiguration.HTTP_STATUS_TABLE);
        translator.setProtocolTable(ErrorConfiguration.PROTOCOL_TRANSLATE_TABLE);
    }

    @Test
    public void testProtocolException() throws ServerException {
        final PortalProtocolException exception = new AuthenticationException(ProtocolError.AUTHENTICATION_REJECTED, "rejected");

        final PlatformException ex = new PlatformException(translator.translate(exception),
                exception.getMessage(), exception);

        Assert.assertEquals(81, ex.getPortalError().getValue());
    }

    @Test
    public void testPortalErrorTranslator() {
        final PortalError
                maintenance = PortalError.SERVER_MAINTENANCE,
                invalidRequest = PortalError.INVALID_REQUEST,
                badCredentials = PortalError.BAD_CLIENT_CREDENTIALS,
                nasUnreachable = PortalError.NAS_UNREACHABLE,
                nasNotRespond = PortalError.NAS_NOT_RESPOND,
                apiUpgraded = PortalError.SERVER_API_UPGRADED,
                needSsl = PortalError.NEED_SSL,
                sessionNotFound = PortalError.SESSION_NOT_FOUND,
                unavailable = PortalError.SERVICE_UNAVAILABLE,
                tooManyRequests = PortalError.REST_REQUEST_RATE_LIMITED,
                invalidScope = PortalError.INVALID_SCOPE,
                unprocessable = PortalError.UNPROCESSABLE_ENTITY,
                invalidAuthentication = PortalError.INVALID_AUTHENTICATE_CREDENTIALS;


        Assert.assertEquals(HttpStatus.BAD_GATEWAY.value(), translator.translate(maintenance));

        Assert.assertEquals(HttpStatus.BAD_REQUEST.value(), translator.translate(invalidRequest));

        Assert.assertEquals(HttpStatus.FORBIDDEN.value(), translator.translate(invalidAuthentication));

        Assert.assertEquals(HttpStatus.UNAUTHORIZED.value(), translator.translate(badCredentials));

        Assert.assertEquals(HttpStatus.GATEWAY_TIMEOUT.value(), translator.translate(nasUnreachable));
        Assert.assertEquals(HttpStatus.GATEWAY_TIMEOUT.value(), translator.translate(nasNotRespond));

        Assert.assertEquals(HttpStatus.GONE.value(), translator.translate(apiUpgraded));

        Assert.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), translator.translate(needSsl));

        Assert.assertEquals(HttpStatus.NOT_FOUND.value(), translator.translate(sessionNotFound));

        Assert.assertEquals(HttpStatus.SERVICE_UNAVAILABLE.value(), translator.translate(unavailable));

        Assert.assertEquals(HttpStatus.TOO_MANY_REQUESTS.value(), translator.translate(tooManyRequests));

        Assert.assertEquals(HttpStatus.UNAUTHORIZED.value(), translator.translate(invalidScope));

        Assert.assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.value(), translator.translate(unprocessable));

    }
}
