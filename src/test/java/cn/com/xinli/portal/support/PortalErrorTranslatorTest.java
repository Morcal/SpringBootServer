package cn.com.xinli.portal.support;

import cn.com.xinli.portal.core.PortalError;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.http.HttpStatus;

/**
 * Project: xpws
 *
 * @author zhoupeng 2016/1/20.
 */
public class PortalErrorTranslatorTest {
    @Test
    public void testPortalErrorTranslator() {
        final PortalError
                maintenance = PortalError.of("server_maintenance"),
                invalidRequest = PortalError.of("invalid_request"),
                badCredentials = PortalError.of("bad_client_credentials"),
                nasUnreachable = PortalError.of("nas_unreachable"),
                nasNotRespond = PortalError.of("nas_not_respond"),
                apiUpgraded = PortalError.of("server_api_upgraded"),
                needSsl = PortalError.of("need_ssl"),
                sessionNotFound = PortalError.of("session_not_found"),
                unavailable = PortalError.of("service_unavailable"),
                tooManyRequests = PortalError.of("rest_request_rate_limited"),
                invalidScope = PortalError.of("invalid_scope"),
                unprocessable = PortalError.of("unprocessable_entity"),
                invalidAuthentication = PortalError.of("invalid_authenticate_credentials");


        Assert.assertEquals(HttpStatus.BAD_GATEWAY.value(), PortalErrorTranslator.translate(maintenance));

        Assert.assertEquals(HttpStatus.BAD_REQUEST.value(), PortalErrorTranslator.translate(invalidRequest));

        Assert.assertEquals(HttpStatus.FORBIDDEN.value(), PortalErrorTranslator.translate(invalidAuthentication));

        Assert.assertEquals(HttpStatus.UNAUTHORIZED.value(), PortalErrorTranslator.translate(badCredentials));

        Assert.assertEquals(HttpStatus.GATEWAY_TIMEOUT.value(), PortalErrorTranslator.translate(nasUnreachable));
        Assert.assertEquals(HttpStatus.GATEWAY_TIMEOUT.value(), PortalErrorTranslator.translate(nasNotRespond));

        Assert.assertEquals(HttpStatus.GONE.value(), PortalErrorTranslator.translate(apiUpgraded));

        Assert.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), PortalErrorTranslator.translate(needSsl));

        Assert.assertEquals(HttpStatus.NOT_FOUND.value(), PortalErrorTranslator.translate(sessionNotFound));

        Assert.assertEquals(HttpStatus.SERVICE_UNAVAILABLE.value(), PortalErrorTranslator.translate(unavailable));

        Assert.assertEquals(HttpStatus.TOO_MANY_REQUESTS.value(), PortalErrorTranslator.translate(tooManyRequests));

        Assert.assertEquals(HttpStatus.UNAUTHORIZED.value(), PortalErrorTranslator.translate(invalidScope));

        Assert.assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.value(), PortalErrorTranslator.translate(unprocessable));

    }
}
