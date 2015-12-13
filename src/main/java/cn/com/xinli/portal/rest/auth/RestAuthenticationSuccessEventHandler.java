package cn.com.xinli.portal.rest.auth;

import cn.com.xinli.portal.rest.RestAuthenticationSuccessEvent;
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
