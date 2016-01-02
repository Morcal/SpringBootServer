package cn.com.xinli.portal.support;

import cn.com.xinli.portal.Credentials;
import cn.com.xinli.portal.util.CredentialsModifier;
import org.apache.commons.lang3.StringUtils;

/**
 * Prefix Postfix Credentials Modifier
 *
 * Project: xpws
 *
 * @author zhoupeng 2015/12/30.
 */
public class PrefixPostfixCredentialsModifier implements CredentialsModifier {
    private final Target target;
    private final Position position;
    private final String value;

    public PrefixPostfixCredentialsModifier(Target target, Position position, String value) {
        this.target = target;
        this.position = position;
        this.value = value;
    }

    boolean isEmpty() {
        return StringUtils.isEmpty(value) || target == null || position == null;
    }

    private String modify(String source) {
        StringBuilder builder = new StringBuilder();
        switch (position) {
            case HEAD:
                if (!StringUtils.isEmpty(source)) {
                    if (!StringUtils.isEmpty(value)) {
                        builder.append(value);
                    }
                    builder.append(StringUtils.defaultString(source, ""));
                }
                break;

            case TAIL:
                builder.append(StringUtils.defaultString(source, ""));
                if (!StringUtils.isEmpty(value)) {
                    builder.append(value);
                }
                break;

            default:
                break;
        }
        return builder.toString();
    }

    @Override
    public Credentials modify(Credentials credentials) {
        if (isEmpty()) {
            return credentials;
        }

        String username = credentials.getUsername(),
                password = credentials.getPassword();
        switch (target) {
            case USERNAME:
                username = modify(username);
                break;

            case PASSWORD:
                password = modify(password);
                break;
            default:
                break;
        }

        return new Credentials(username, password, credentials.getIp(), credentials.getMac());
    }
}
