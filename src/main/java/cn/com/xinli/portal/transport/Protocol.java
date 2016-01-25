package cn.com.xinli.portal.transport;

import cn.com.xinli.portal.core.NasType;

/**
 * Portal protocol.
 *
 * <p>Portal protocols should be implemented as <em>stateless</em>,
 * so that Protocol client factory can employee the
 * "flyweight" design pattern (one of the GoF design patters).
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2015/12/22.
 */
public interface Protocol<T> {
    /**
     * Get protocol version.
     *
     * @return protocol version.
     */
    int getVersion();

    /**
     * Get supported Nas type name.
     *
     * @return supported nas type name.
     */
    NasType[] getSupportedNasTypes();

    /**
     * Get protocol codec factory.
     *
     * @return protocol codec factory.
     */
    CodecFactory<T> getCodecFactory();
}
