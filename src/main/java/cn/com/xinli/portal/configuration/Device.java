package cn.com.xinli.portal.configuration;

/**
 * Device (NAS/BRAS) support Portal protocol configuration.
 * Project: portal
 *
 * @author zhoupeng 2015/12/2.
 */
public class Device {
    /** IPv4 Address. */
    private final String ipAddress;

    /** Device type. */
    private final String type;

    /** Portal listen port. */
    private final int listenPort;

    /** Authentication type (PAP/CHAP). */
    private final String authType;

    public Device(String ipAddress, String type, int listenPort, String authType) {
        this.ipAddress = ipAddress;
        this.type = type;
        this.listenPort = listenPort;
        this.authType = authType;
    }
}
