package cn.com.xinli.portal.core.radius;

/**
  * <p>Project: xpws
 *
 * @author zhoupeng 2016/2/1.
 */
public interface RadiusManager {
    Radius create(Radius radius);

    void delete(long id) throws RadiusNotFoundException;
}
