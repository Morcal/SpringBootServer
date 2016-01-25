package cn.com.xinli.portal.web.auth;

/**
 * PWS REST Role.
 *
 * <p>PWS defined roles.
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2015/12/28.
 */
public enum RestRole {
    /** Normal users role,
     * REST APIs requires this role.
     */
    USER,
    /** System administration role. */
    ADMIN
}
