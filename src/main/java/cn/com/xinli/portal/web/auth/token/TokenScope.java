package cn.com.xinli.portal.web.auth.token;

import org.springframework.security.core.token.Token;

import java.util.Optional;
import java.util.stream.Stream;

/**
 * Token scope.
 *
 * <p>Each scope represents a set of system facilities.
 * Clients possess a {@link Token} with specific scope
 * meanings that those clients has authorities to access
 * facilities the scope covers.
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2015/12/28.
 */
public enum TokenScope {
    /**
     * System administration token scope.
     * Users possess token with this scope have all authorities
     * of PWS.
     */
    SYSTEM_ADMIN_TOKEN_SCOPE("system-admin"),
    /**
     * REST API scope, users possess token with this scope
     * can access portal REST APIs.
     */
    PORTAL_ACCESS_TOKEN_SCOPE("portal-rest-api"),
    /**
     * Context scope, users possess token with this scope
     * can reattach to existed session.
     */
    PORTAL_CONTEXT_TOKEN_SCOPE("portal-context"),
    /**
     * Session scope, users posses token with this scope
     * can access associated session if presents.
     */
    PORTAL_SESSION_TOKEN_SCOPE("portal-session");

    /** Scope alias name. */
    private final String alias;

    TokenScope(String alias) {
        this.alias = alias;
    }

    /**
     * Get alias name.
     * @return alias name.
     */
    public String alias() {
        return alias;
    }

    /**
     * Convert an alias name to a token scope.
     * @param alias scope alias.
     * @return token scope.
     */
    public static Optional<TokenScope> of(String alias) {
        return Stream.of(TokenScope.values())
                .filter(scope -> scope.alias.equals(alias))
                .findAny();
    }
}
