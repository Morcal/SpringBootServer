package cn.com.xinli.portal.transport;

/**
 * Transport error container.
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2016/1/19.
 */
public interface TransportErrorContainer {
    /**
     * Get protocol error.
     * @return protocol error.
     */
    TransportError getProtocolError();
}
