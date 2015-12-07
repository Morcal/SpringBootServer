package cn.com.xinli.portal.util;

import cn.com.xinli.portal.Credential;

/**
 * Project: portal
 *
 * @author zhoupeng 2015/12/6.
 */
public class TokenCredential implements Credential {



    @Override
    public boolean authorized() {
        return false;
    }
}
