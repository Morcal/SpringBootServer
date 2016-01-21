package cn.com.xinli.portal.core;

/**
 * Portal error container.
 *
 * <p>System defined exceptions which associate with global defined errors
 * should implement this interface.
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2016/1/19.
 */
public interface PortalErrorContainer {
    /**
     * Get portal error.
     * @return portal error.
     */
    PortalError getPortalError();
}
