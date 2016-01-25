package cn.com.xinli.portal.transport.huawei;

import java.util.Optional;
import java.util.stream.Stream;

/**
 * Huawei Protocol Authentication error.
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2016/1/24.
 */
public enum AuthError {
    OK(0x00, "认证成功"),
    REJECTED(0x01, "认证请求被拒绝"),
    ALREADY_ONLINE(0x02, "此链接已经建立"),
    WAIT(0x03, "正在认证请稍候"),
    FAILED(0x04, "认证失败");

    private final int code;
    private final String description;

    AuthError(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int code() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static Optional<AuthError> valueOf(int code) {
        return Stream.of(values()).filter(v -> v.code() == code).findFirst();
    }
}
