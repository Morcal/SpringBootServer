package cn.com.xinli.portal.core.nas;

import cn.com.xinli.portal.core.DataStore;
import cn.com.xinli.portal.core.Locatable;
import cn.com.xinli.portal.core.RoutingMapper;
import org.apache.commons.lang3.tuple.Pair;

import java.util.stream.Stream;

/**
 * NAS data store.
 *
 * <p>Classes implement this interface contain configured NAS/BRAS
 * devices, and those devices can be retrieved by calling
 * {@link #get(Long)} and {@link #find(String)}.
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2016/1/30.
 */
public interface NasStore extends RoutingMapper, Locatable<Pair<String, String>, Nas>, DataStore<Nas, Long> {
    /**
     * Get NAS/BRAS by device id.
     * @param id NAS device id.
     * @return NAS if found.
     * @throws NasNotFoundException
     */
    @Override
    Nas get(Long id) throws NasNotFoundException;

    @Override
    boolean delete(Long id) throws NasNotFoundException;

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
