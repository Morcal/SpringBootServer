package cn.com.xinli.portal.core.nas;

import javax.persistence.Column;
import javax.persistence.Entity;

/**
 * NAS/BRAS devices support HUAWEI portal protocols.
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2016/1/30.
 */
@Entity
public class HuaweiNas extends Nas {
    /** Portal server shared secret. */
    @Column(name = "shared_secret", nullable = false)
    private String sharedSecret;

    /** Portal listen port. */
    @Column(name = "listen_port", nullable = false)
    private int listenPort;

    /** Authentication type (PAP/CHAP). */
    @Column(name = "authentication_type", nullable = false)
    private String authType;

    /** HUAWEI portal protocol version. */
    private String version;

    @Override
    protected NasType getType() {
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
