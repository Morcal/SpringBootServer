package cn.com.xinli.portal.rest.api.v1.auth;

import cn.com.xinli.portal.rest.api.RestAuthenticationSuccessEvent;
import org.springframework.context.ApplicationListener;

/**
 * Project: portal
 *
 * @author zhoupeng 2015/12/11.
 */
public class RestAuthenticationSuccessEventHandler implements ApplicationListener<RestAuthenticationSuccessEvent> {
    @Override
    public void onApplicationEvent(RestAuthenticationSuccessEvent event) {

    }
}
