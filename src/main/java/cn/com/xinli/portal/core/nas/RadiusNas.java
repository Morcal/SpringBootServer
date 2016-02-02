package cn.com.xinli.portal.core.nas;

import cn.com.xinli.nps.EndPoint;
import cn.com.xinli.nps.NetPolicyServer;
import cn.com.xinli.portal.core.radius.Radius;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;
import java.util.List;

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
@DiscriminatorValue("RADIUS")
@JsonInclude
public class RadiusNas extends Nas {
    /** Net policy server type. */
    public enum NPSType {
        /** Net policy server is integrated with portal web server. */
        LOCAL,
        /** Net policy server is remote-standalone. */
        REMOTE
    }

    /** NPS type. */
    @Column(name = "nps_type")
    @JsonProperty("nps_type")
    private NPSType npsType;

    /** Portal server shared secret. */
    @Column(name = "nps_shared_secret")
    @JsonProperty("nps_shared_secret")
    private String sharedSecret;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(schema = "PWS", name="nas_radius",
            joinColumns = @JoinColumn(name = "nas_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "radius_id", referencedColumnName = "id"))
    @JsonProperty
    private List<Radius> radiusServers;

    public List<Radius> getRadiusServers() {
        return radiusServers;
    }

    public void setRadiusServers(List<Radius> radiusServers) {
        this.radiusServers = radiusServers;
    }

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
    public NasType getType() {
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
