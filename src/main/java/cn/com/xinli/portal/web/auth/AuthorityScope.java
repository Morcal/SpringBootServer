package cn.com.xinli.portal.web.auth;

/**
 * System authority scope.
 *
  * <p>Project: xpws
 *
 * @author zhoupeng 2015/12/29.
 */
public enum AuthorityScope {
    /** System administration token scope.
     * Users possess token with this scope have all authorities
     * of PWS.
     */
    SYSTEM_ADMIN_SCOPE,
    /** REST API scope, users possess token with this scope
     * can access portal REST APIs.
     */
    PORTAL_ACCESS_SCOPE,
    /**
     * Session scope, users posses token with this scope
     * can access associated session if presents.
     */
    PORTAL_SESSION_SCOPE
}
