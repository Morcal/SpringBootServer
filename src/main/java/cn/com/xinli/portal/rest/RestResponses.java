package cn.com.xinli.portal.rest;

import cn.com.xinli.portal.rest.bean.RestBean;

/**
 * Project: xpws
 *
 * @author zhoupeng 2015/12/19.
 */
public class RestResponses {

    public static RestBean invalidRequest() {
        return RestResponseBuilders.errorBuilder().build();
    }

    public static RestBean invalidPortalRequest() {
        return RestResponseBuilders.errorBuilder().build();
    }
}
