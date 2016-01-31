package cn.com.xinli.portal.core.nas;

import javax.persistence.Entity;

/**
 * NAS/BRAS devices support CMCC portal protocols.
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2016/1/30.
 */
@Entity
public class CmccNas extends Nas {

    @Override
    protected NasType getType() {
        return NasType.CMCC;
    }
}
