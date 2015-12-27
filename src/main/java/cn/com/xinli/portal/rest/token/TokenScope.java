package cn.com.xinli.portal.rest.token;

import java.util.Optional;
import java.util.stream.Stream;

/**
 * Project: xpws
 *
 * @author zhoupeng 2015/12/28.
 */
public enum TokenScope {
    SYSTEM_ADMIN_TOKEN_SCOPE("system-admin"),
    PORTAL_ACCESS_TOKEN_SCOPE("portal-rest-api"),
    PORTAL_SESSION_TOKEN_SCOPE("portal-session");

    private final String alias;

    TokenScope(String alias) {
        this.alias = alias;
    }

    public String alias() {
        return alias;
    }

    public static Optional<TokenScope> ofAlias(String alias) {
        return Stream.of(TokenScope.values())
                .filter(scope -> scope.alias.equals(alias))
                .findAny();
    }
}
