package cn.com.xinli.portal.core.nas;

import cn.com.xinli.portal.core.RoutingMapper;
import cn.com.xinli.portal.core.Locatable;
import cn.com.xinli.portal.core.credentials.Credentials;
import cn.com.xinli.portal.core.session.Session;

/**
 * NAS locator.
 *
 * <p>Server maintains a mapping which keeps
 * tracking which NAS the client came from. When clients request to operation
 * on {@link Session}s, server can retrieve the
 * originate NAS/BRAS from that mapping, and then communicate with target NAS/BRAS.
 *
 * <p>When client/user access PWS entry for the first time,
 * PWS records client/user's information and the {@link Nas} information
 * by creating a ip/mac to NAS configuration mapping.
 *
 * <p>PWS uses this mapping to handle client/user's portal
 * login/logout requests, and will communicate with mapped
 * {@link Nas}.
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2016/1/23.
 */
public interface NasLocator extends Locatable<Credentials, Nas>, RoutingMapper {
    /**
     * {@inheritDoc}
     *
     * <p>Locate user originate routing NAS.
     *
     * @param credentials user credentials.
     * @return originate NAS.
     * @throws NasNotFoundException
     */
    @Override
    Nas locate(Credentials credentials) throws NasNotFoundException;
}
