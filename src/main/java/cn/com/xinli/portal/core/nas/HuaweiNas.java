package cn.com.xinli.portal.core.nas;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * NAS/BRAS devices support HUAWEI portal protocols.
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2016/1/30.
 */
@Entity
@DiscriminatorValue("HUAWEI")
@JsonInclude
public class HuaweiNas extends Nas {
    /** Portal server shared secret. */
    @Column(name = "portal_shared_secret")
    @JsonProperty("portal_shared_secret")
    private String sharedSecret;

    /** Portal listen port. */
    @Column(name = "listen_port")
    @JsonProperty("listen_port")
    private int listenPort;

    /** Authentication type (PAP/CHAP). */
    @Column(name = "authentication_type")
    @JsonProperty("authentication_type")
    private String authType;

    /** HUAWEI portal protocol version. */
    @JsonProperty
    private String version;

    @Override
    public NasType getType() {
        return NasType.HUAWEI;
    }

    /**
     * Get authentication type.
     * @return authentication type.
     */
    public String getAuthType() {
        return authType;
    }

    public void setAuthType(String authType) {
        this.authType = authType;
    }

    /**
     * Get nas portal listen port.
     * @return portal listen port.
     */
    public int getListenPort() {
        return listenPort;
    }

    public void setListenPort(int listenPort) {
        this.listenPort = listenPort;
    }

    public String getSharedSecret() {
        return sharedSecret;
    }

    public void setSharedSecret(String sharedSecret) {
        this.sharedSecret = sharedSecret;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return super.toString() +  "HuaweiNas{" +
                "sharedSecret='" + sharedSecret + '\'' +
                ", listenPort=" + listenPort +
                ", authType='" + authType + '\'' +
                ", version='" + version + '\'' +
                '}';
    }
}
