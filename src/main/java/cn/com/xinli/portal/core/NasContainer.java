package cn.com.xinli.portal.core;

/**
 * Nas container.
 *
 * <p>Classes implement this interface contain configured NAS/BRAS
 * devices, and those devices can be retrieved by calling
 * {@link #get(long)} and {@link #get(String)}.
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2016/1/24.
 */
public interface NasContainer {
    /**
     * Get NAS/BRAS by device id.
     * @param id NAS id.
     * @return NAS if found.
     * @throws NasNotFoundException
     */
    Nas get(long id) throws NasNotFoundException;

    /**
     * Find NAS/BRAS by device ip address.
     * @param ip ip address.
     * @return NAS if found.
     * @throws NasNotFoundException
     */
    Nas get(String ip) throws NasNotFoundException;

    /**
     * Find NAS/BRAS by device name.
     * @param name device name.
     * @return NAS if found.
     * @throws NasNotFoundException
     */
    Nas find(String name) throws NasNotFoundException;
}
