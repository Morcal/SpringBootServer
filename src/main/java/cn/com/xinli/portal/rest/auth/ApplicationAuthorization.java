package cn.com.xinli.portal.rest.auth;


import org.apache.commons.lang3.StringUtils;
import javax.validation.constraints.NotNull;

/**
 * Application Authorization.
 *
 * <p>Application with this authorization can access services.
 * Application authorization key MUST be unique.
 * </p>
 * Project: portal
 *
 * @author zhoupeng 2015/12/5.
 */
public class ApplicationAuthorization {

    /** Application key issued by PWS when register authorized application. */
    private final String key;

    /** Application authorization shared secret key. */
    private final String sharedSecret;

    public ApplicationAuthorization(@NotNull String key, @NotNull String sharedSecret) {
        this.key = key;
        this.sharedSecret = sharedSecret;
    }

    @Override
    public int hashCode() {
        return key.hashCode();
    }

    public boolean matches(String sharedSecret) {
        if (StringUtils.isEmpty(sharedSecret)) {
            throw new IllegalArgumentException("Application Authorization sharedKey is empty.");
        }
        return this.sharedSecret.equals(sharedSecret);
    }
}
