package cn.com.xinli.portal.transport.huawei;

import java.util.Optional;
import java.util.stream.Stream;

/**
 * Huawei Protocol logout error.
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2016/1/24.
 */
public enum LogoutError {
    OK(0x00, "下线成功"),
    REJECTED(0x01, "下线被拒绝"),
    FAILED(0x02, "下线失败"),
    GONE(0x03, "此用户已经下线");

    private final int code;
    private final String description;

    LogoutError(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int code() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static Optional<LogoutError> valueOf(int code) {
        return Stream.of(values()).filter(v -> v.code() == code).findFirst();
    }
}

