package cn.com.xinli.portal.protocol;

import cn.com.xinli.portal.Message;

import java.io.IOException;

/**
 * Portal Client.
 *
 * Project: xpws
 *
 * @author zhoupeng 2015/12/22.
 */
public interface PortalClient {

    Message login(Credentials credentials) throws IOException;

    Message logout(Credentials credentials) throws IOException;
}
