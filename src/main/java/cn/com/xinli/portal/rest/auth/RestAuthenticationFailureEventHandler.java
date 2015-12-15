package cn.com.xinli.portal.rest.auth;

import cn.com.xinli.portal.rest.RestAuthenticationFailureEvent;
import cn.com.xinli.portal.rest.RestResponse;
import cn.com.xinli.portal.rest.RestResponseBuilders;
import cn.com.xinli.portal.rest.bean.Failure;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationListener;

import javax.servlet.ServletOutputStream;
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
        HttpServletResponse response = event.getResponse();
        try {
            Failure failure = RestResponseBuilders.errorBuilder()
                    .setError(RestResponse.ERROR_INVALID_CLIENT)
                    .setDescription("description").build();
            String json = new ObjectMapper().writeValueAsString(failure);
            ServletOutputStream output = response.getOutputStream();
            output.println(json);
            output.flush();
            output.close();
        } catch (IOException e) {
            if (log.isDebugEnabled()) {
                log.debug("send servlet response failed, ", e);
            }
            log.warn("failed to send failure response.");
        }
    }
}
