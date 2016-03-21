package cn.com.xinli.portal.core.nas;

import cn.com.xinli.portal.core.RoutingMapper;
import cn.com.xinli.portal.core.DataStore;
import cn.com.xinli.portal.core.Locatable;
import cn.com.xinli.portal.core.credentials.Credentials;
import org.apache.commons.lang3.tuple.Pair;

import java.util.stream.Stream;

/**
 * NAS data store.
 *
 * <p>Classes implement this interface contain configured NAS/BRAS
 * devices, and those devices can be retrieved by calling
 * {@link #get(String)} and {@link #get(String)}.
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2016/1/30.
 */
public interface NasStore extends RoutingMapper, Locatable<Pair<String, String>, Nas>, DataStore<Nas, String> {
    /**
     * Get NAS/BRAS by device id.
     * @param name NAS device name.
     * @return NAS if found.
     * @throws NasNotFoundException
     */
    @Override
    Nas get(String name) throws NasNotFoundException;

    @Override
    boolean delete(String key) throws NasNotFoundException;

    @Override
    Nas locate(Pair<String, String> pair) throws NasNotFoundException;

    /**
     * Find NAS/BRAS by device ip address.
     * @param ip ip address.
     * @return NAS if found.
     * @throws NasNotFoundException
     */
    Nas find(String ip) throws NasNotFoundException;

    NasRule put(NasRule rule);

    Stream<NasRule> rules();

    Stream<Nas> devices();

    void reload();

    Stream<Nas> search(String value);
}
