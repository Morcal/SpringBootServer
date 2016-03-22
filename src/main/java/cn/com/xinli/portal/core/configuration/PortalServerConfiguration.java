package cn.com.xinli.portal.core.configuration;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Portal Server Configuration.
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2016/1/29.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PortalServerConfiguration {
    /** Portal server name. */
    @JsonProperty
    private String name;

    /** Host. */
    @JsonProperty
    private String host;

    /** Port. */
    @JsonProperty
    private int port;

    /** Shared secret. */
    @JsonProperty("shared_secret")
    private String sharedSecret;

    /** Portal version. */
    @JsonProperty
    private String version;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
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
        return "PortalServerConfiguration{" +
                "name='" + name + '\'' +
                ", host='" + host + '\'' +
                ", port=" + port +
                ", sharedSecret='" + sharedSecret + '\'' +
                ", version='" + version + '\'' +
                '}';
    }
}
