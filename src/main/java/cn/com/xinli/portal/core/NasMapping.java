package cn.com.xinli.portal.core;

/**
 * Nas Mapping.
 *
 * <p>This class contains NAS/BRAS and clients mappings.
 *
 * <p>When client/user access PWS entry for the first time,
 * PWS records client/user's information and the {@link Nas} information
 * by creating a ip/mac to NAS configuration mapping.
 *
 * <p>PWS uses this mapping to handle client/user's portal
 * login/logout requests, and will communicate with mapped
 * {@link Nas}.
 *
 * <p>Project: xpws.
 *
 * @author zhoupeng 2015/12/2.
 */
@FunctionalInterface
public interface NasMapping {
    /**
     * Create a mapping from user ip and mac to NAS device configuration.
     *
     * @param credentials User credentials.
     * @param nas NAS.
     * @throws NasNotFoundException
     */
    void map(Credentials credentials, Nas nas) throws NasNotFoundException;
}
