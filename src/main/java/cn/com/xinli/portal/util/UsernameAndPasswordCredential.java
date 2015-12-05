package cn.com.xinli.portal.util;

import cn.com.xinli.portal.Credential;

/**
 * Project: portal
 *
 * @author zhoupeng 2015/12/2.
 */
public class UsernameAndPasswordCredential implements Credential {
    private boolean authorized = false;

    private final String username;

    private final String password;

    public UsernameAndPasswordCredential(String username, String password) {
        this.username = username;
        this.password = password;
    }

    @Override
    public boolean authorized() {
        return authorized;
    }
}
