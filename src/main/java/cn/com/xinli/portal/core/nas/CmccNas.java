package cn.com.xinli.portal.core.nas;

import com.fasterxml.jackson.annotation.JsonInclude;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * NAS/BRAS devices support CMCC portal protocols.
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2016/1/30.
 */
@Entity
@JsonInclude
@DiscriminatorValue("CMCC")
public class CmccNas extends Nas {

    @Override
    public NasType getType() {
        return NasType.CMCC;
    }
}
