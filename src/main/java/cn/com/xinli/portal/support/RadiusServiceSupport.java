package cn.com.xinli.portal.support;

import cn.com.xinli.portal.core.radius.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * Radius Service Support.
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2016/2/1.
 */
@Service
public class RadiusServiceSupport implements RadiusService, RadiusManager {
    @Autowired
    private RadiusStore radiusStore;

    @Override
    public Radius create(Radius radius) {
        Objects.requireNonNull(radius, Radius.EMPTY_RADIUS);
        radiusStore.put(radius);
        return radius;
    }

    @Override
    public void delete(long id) throws RadiusNotFoundException {
        radiusStore.delete(id);
    }
}
