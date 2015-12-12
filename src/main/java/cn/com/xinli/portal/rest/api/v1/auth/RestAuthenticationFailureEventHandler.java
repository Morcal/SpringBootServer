package cn.com.xinli.portal.rest.api.v1.auth;

import cn.com.xinli.portal.rest.RestResponse;
import cn.com.xinli.portal.rest.RestResponseBuilders;
import cn.com.xinli.portal.rest.api.RestAuthenticationFailureEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.json.JacksonJsonParser;
import org.springframework.context.ApplicationListener;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Project: portal
 *
 * @author zhoupeng 2015/12/11.
 */
public class RestAuthenticationFailureEventHandler implements ApplicationListener<RestAuthenticationFailureEvent> {
    /** Log. */
    private static final Log log = LogFactory.getLog(RestAuthenticationFailureEventHandler.class);

    @Override
    public void onApplicationEvent(RestAuthenticationFailureEvent event) {
        JacksonJsonParser parser;
        try {
            HttpServletResponse response = event.getResponse();
            RestResponse.Error res = RestResponseBuilders.errorBuilder().setError(RestResponse.ERROR_INVALID_CLIENT)
                    .setDescription("description").build();
            String json = new ObjectMapper().writeValueAsString(res);
            response.setStatus(HttpServletResponse.SC_OK);
            response.addHeader("Content-Type", "application/json");
            response.getWriter().write(json);
        } catch (IOException e) {
            if (log.isDebugEnabled()) {
                log.debug("send servlet response failed, ", e);
            }
            log.warn("failed to send failure response.");
        }
    }
}
