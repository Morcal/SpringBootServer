package cn.com.xinli.portal.protocol;

/**
 * Portal protocol.
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2015/12/22.
 */
public interface Protocol {
    /**
     * Get protocol version.
     * @return protocol version.
     */
    int getVersion();

    /**
     * Get supported Nas type name.
     * @return supported nas type name.
     */
    String[] getSupportedNasTypeName();

    /**
     * Get protocol codec factory.
     * @return protocol codec factory.
     */
    CodecFactory getCodecFactory();
}
