package cn.com.xinli.portal.transport.huawei;

/**
 * Huawei logout request error.
 *
 * <p>This error will be used when portal client requests for logout to
 * remote portal server. Normal logout requests should use {@link #REQUEST}
 * as error code to identify themselves as normal requests distinguish from
 * timeout notifications which should use {@link #REQUEST_TIMEOUT} as error code.
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2016/1/24.
 */
public enum LogoutRequestError {
    /** Normal logout request. */
    REQUEST(0x00),

    /** Logout timeout notification request. */
    REQUEST_TIMEOUT(0x01);

    private final int code;

    LogoutRequestError(int code) {
        this.code = code;
    }

    public int code() {
        return code;
    }
}
