package cn.com.xinli.portal.core.nas;

import cn.com.xinli.portal.core.credentials.Credentials;
import cn.com.xinli.portal.util.AddressUtil;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;

/**
 * IPv4 range based NAS rule.
 *
 * <p>This class provides a IPv4 ranged rule for credentials-NAS matching.
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2016/1/23.
 */
@Entity
@DiscriminatorValue("IPv4-RANGE")
public class IPv4RangeBasedNasRule extends NasRule {
    /** IPv4 Start. */
    @Column(name = "ipv4_start")
    private String ipv4start;

    /** IPv4 end. */
    @Column(name = "ipv4_end")
    private String ipv4end;

    @Transient
    private int start;

    @Transient
    private int end;

    public String getIpv4start() {
        return ipv4start;
    }

    public void setIpv4start(String ipv4start) {
        this.ipv4start = ipv4start;
    }

    public String getIpv4end() {
        return ipv4end;
    }

    public void setIpv4end(String ipv4end) {
        this.ipv4end = ipv4end;
    }

    @Override
    public boolean matchInternal(Credentials credentials) {
        //FIXME take another more efficient approaches.
        synchronized (this) {
            if (start == 0 && end == 0) {
                start = AddressUtil.convertIpv4Address(ipv4start);
                end = AddressUtil.convertIpv4Address(ipv4end);
            }
        }
        String ip = credentials.getIp();
        int v = AddressUtil.convertIpv4Address(ip);

        return v >= Math.min(start, end) && v <= Math.max(start, end);
    }
}
