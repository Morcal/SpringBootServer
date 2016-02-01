package cn.com.xinli.portal.support.ehcache;

import cn.com.xinli.portal.core.radius.Radius;
import cn.com.xinli.portal.core.radius.RadiusNotFoundException;
import cn.com.xinli.portal.core.radius.RadiusStore;
import cn.com.xinli.portal.support.persist.RadiusPersistence;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * Radius store based on <a href="http://ehcache.org">EhCache</a>.
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2016/2/1.
 */
@Component
@Profile("standalone")
public class EhcacheRadiusStore implements RadiusStore {
    @Autowired
    private Ehcache radiusCache;

    @Autowired
    private RadiusPersistence radiusPersistence;

    Element toElement(Radius radius) {
        return new Element(radius.getId(), radius);
    }

    @Override
    public Radius get(Long id) throws RadiusNotFoundException {
        Element element = radiusCache.get(id);
        if (element == null) {
            throw new RadiusNotFoundException("id:" + id);
        }

        return (Radius) element.getObjectValue();
    }

    @Override
    public void put(Radius radius) {
        Objects.requireNonNull(radius);
        radiusPersistence.save(radius);
        radiusCache.put(toElement(radius));
    }

    @Override
    public boolean exists(Long id) {
        return radiusCache.get(id) != null;
    }

    @Override
    public boolean delete(Long id) throws RadiusNotFoundException {
        radiusPersistence.delete(id);
        return radiusCache.remove(id);
    }
}
