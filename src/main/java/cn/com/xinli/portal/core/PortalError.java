package cn.com.xinli.portal.core;

/**
 * Portal RestError.
 *
 * Project: xpws
 *
 * @author zhoupeng 2016/1/16.
 */
public class PortalError {
    /** RestError code. */
    private final int code;

    /** RestError description. */
    private final String description;

    private String information;

    PortalError(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public PortalError SERVER_INTERNAL_ERROR = of(13, "Server Internal RestError");
    public PortalError SERVICE_UNAVAILABLE = of(14, "Service Unavailable");

    private static PortalError of(int code, String description) {
        return new PortalError(code, description);
    }
}
