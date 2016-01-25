package cn.com.xinli.portal.transport.huawei;

import java.util.Optional;
import java.util.stream.Stream;

/**
 * Huawei Protocol Challenge error.
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2016/1/24.
 */
public enum ChallengeError {
    OK(0x00, "Challenge成功"),
    REJECTED(0x01, "Challenge被拒绝"),
    ALREADY_ONLINE(0x02, "此链接已经建立"),
    WAIT(0x03, "正在认证请稍候"),
    FAILED(0x04, "Challenge失败");

    private final int code;
    private final String description;

    ChallengeError(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int code() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static Optional<ChallengeError> valueOf(int code) {
        return Stream.of(values()).filter(v -> v.code() == code).findFirst();
    }
}

