package cn.com.xinli.portal.core;

/**
 * NAS locator.
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2016/1/23.
 */
@FunctionalInterface
public interface NasLocator {
    /**
     * Locate user incoming NAS.
     *
     * @param credentials user credentials.
     * @return originate NAS.
     * @throws NasNotFoundException
     */
    Nas locate(Credentials credentials) throws NasNotFoundException;
}
