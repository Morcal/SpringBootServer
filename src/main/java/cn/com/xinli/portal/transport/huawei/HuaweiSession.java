package cn.com.xinli.portal.transport.huawei;

import cn.com.xinli.portal.core.session.Session;

/**
 * Project: xpws
 *
 * @author zhoupeng 2016/1/30.
 */
public class HuaweiSession extends Session {
    Endpoint endpoint;

    public Endpoint getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(Endpoint endpoint) {
        this.endpoint = endpoint;
    }

}
