package cn.com.xinli.portal.rest.auth;

import cn.com.xinli.portal.Session;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityMetadataSource;
import org.springframework.security.access.intercept.AbstractSecurityInterceptor;
import org.springframework.security.access.method.MapBasedMethodSecurityMetadataSource;

import java.util.Collection;

/**
 * Project: xpws
 *
 * @author zhoupeng 2015/12/19.
 */
public class SessionSecurityInterceptor extends AbstractSecurityInterceptor {
    @Override
    public Class<?> getSecureObjectClass() {
        return Session.class;
    }

    @Override
    public SecurityMetadataSource obtainSecurityMetadataSource() {
        return null;
    }

    class MetadataSource extends MapBasedMethodSecurityMetadataSource {

    }
}
