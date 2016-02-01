package cn.com.xinli.portal.support.persist;

import cn.com.xinli.portal.core.radius.Radius;
import cn.com.xinli.portal.support.repository.RadiusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * RADIUS Server persistence.
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2016/2/1.
 */
@Component
public class RadiusPersistence {
    @Qualifier("radiusRepository")
    @Autowired
    private RadiusRepository radiusRepository;

    public Radius save(Radius radius) {
        return radiusRepository.save(radius);
    }

    public void delete(Long id) {
        radiusRepository.delete(id);
    }
}
