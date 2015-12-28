package cn.com.xinli.portal.rest.token;

import cn.com.xinli.portal.Session;
import org.springframework.security.core.token.Token;

import java.util.Optional;
import java.util.stream.Stream;

/**
 * Token scope.
 * <p>
 * Each scope represents a set of system facilities.
 * Clients possess a {@link Token} with specific scope
 * meanings that those clients has authorities to access
 * facilities the scope covers.
 * <p>
 * Project: xpws
 *
 * @author zhoupeng 2015/12/28.
 */
public enum TokenScope {
    /** System administration token scope.
     * Users possess token with this scope have all authorities
     * of PWS.
     */
    SYSTEM_ADMIN_TOKEN_SCOPE("system-admin"),
    /** REST API scope, users possess token with this scope
     * can access portal REST APIs.
     */
    PORTAL_ACCESS_TOKEN_SCOPE("portal-rest-api"),
    /**
     * Session scope, users posses token with this scope
     * can access associated {@link Session} if presents.
     */
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
