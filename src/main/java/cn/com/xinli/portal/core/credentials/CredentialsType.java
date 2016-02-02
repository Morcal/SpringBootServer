package cn.com.xinli.portal.core.credentials;

/**
 * Credentials type.
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2016/2/2.
 */
public enum CredentialsType {
    /** Default credentials, can not be used by providers. */
    DEFAULT,

    /** HUAWEI portal protocol supported NAS. */
    HUAWEI,

    /** CMCC portal protocol supported NAS. */
    CMCC,

    /** Net policy server and RADIUS-CoA based NAS. */
    RADIUS
}
