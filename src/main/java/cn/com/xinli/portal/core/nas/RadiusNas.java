package cn.com.xinli.portal.core.nas;

import cn.com.xinli.nps.EndPoint;
import cn.com.xinli.nps.NetPolicyServer;

import javax.persistence.Column;
import javax.persistence.Entity;

/**
 * NAS/BRAS devices support RADIUS portal protocols.
 *
 * <p>In a portal system, network access devices (NAS/BRAS) may not support
 * any portal protocols. In such circumstances, a
 * {@link NetPolicyServer} stands in the central
 * of system, communicates with both NAS/BRAS and RADIUS server using
 * RADIUS protocol.
 *
 * <p>When portal web server try to establish a connection for users came
 * from {@link RadiusNas} devices, server perform authentications with
 * {@link NetPolicyServer}s.
 *
 * <p>This class contains information for portal web server to establish
 * a {@link NetPolicyServer} {@link EndPoint}. If {@link NetPolicyServer} is
 * {@link NPSType#LOCAL}, no additional information needed. If is
 * {@link NPSType#REMOTE}, remote address and shared secret will be provided.
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2016/1/30.
 */
@Entity
public class RadiusNas extends Nas {
    /** Net policy server type. */
    public enum NPSType {
        /** Net policy server is integrated with portal web server. */
        LOCAL,
        /** Net policy server is remote-standalone. */
        REMOTE
    }

    /** NPS type. */
    @Column(name = "nps_type", nullable = false)
    private NPSType npsType;

    /** Portal server shared secret. */
    @Column(name = "shared_secret", nullable = false)
    private String sharedSecret;

    public String getSharedSecret() {
        return sharedSecret;
    }

    public void setSharedSecret(String sharedSecret) {
        this.sharedSecret = sharedSecret;
    }

    public NPSType getNpsType() {
        return npsType;
    }

    public void setNpsType(NPSType npsType) {
        this.npsType = npsType;
    }

    @Override
    protected NasType getType() {
        return NasType.RADIUS;
    }

    @Override
    public String toString() {
        return super.toString() + "RadiusNas{" +
                "npsType=" + npsType +
                ", sharedSecret='" + sharedSecret + '\'' +
                '}';
    }
}
