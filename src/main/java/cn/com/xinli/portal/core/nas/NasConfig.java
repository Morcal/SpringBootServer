package cn.com.xinli.portal.core.nas;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * NAS configuration.
 *
 * <p>This class provides a convenient way to create a NAS by
 * providing a simple plain object.
 *
 * @author zhoupeng, created on 2016/3/26.
 */
public class NasConfig {
    /** NAS name. */
    private String name;

    /** Authentication type. */
    private String authType;

    /** Remote NAS device listen port. */
    private int port;

    /** Remote NAS device ip address. */
    private String host;

    /** shared secret. */
    private String sharedSecret;

    /** Version. */
    private String version;

    /** Ip range supported by NAS device. */
    private String range;

    /**
     * If authenticate with domain.
     *
     * <p>If so, a full authentication username including domain will be send
     * to remote NAS device. If not, and a domain presented in user credentials,
     * the domain will be truncated. For example, if <code>authenticateWithDomain</code> is
     * false, a user named "foobar@example.com" will be authenticated as
     * "foorbar".
     */
    private boolean authenticateWithDomain;

    /** Internal modifier configurations. */
    private List<ModifierConfig> modifiers = new ArrayList<>();

    /**
     * Modifier configuration.
     */
    public static class ModifierConfig {
        /** Modifier position of user credentials. */
        private String position;

        /** Modifier target of user credentials. */
        private String target;

        /** Modifier value of user credentials. */
        private String value;

        public String getPosition() {
            return position;
        }

        public void setPosition(String position) {
            this.position = position;
        }

        public String getTarget() {
            return target;
        }

        public void setTarget(String target) {
            this.target = target;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAuthType() {
        return authType;
    }

    public void setAuthType(String authType) {
        this.authType = authType;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
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

    public String getRange() {
        return range;
    }

    public void setRange(String range) {
        this.range = range;
    }

    public boolean isAuthenticateWithDomain() {
        return authenticateWithDomain;
    }

    public void setAuthenticateWithDomain(boolean authenticateWithDomain) {
        this.authenticateWithDomain = authenticateWithDomain;
    }

    public List<ModifierConfig> getModifiers() {
        return modifiers;
    }

    public void setModifiers(List<ModifierConfig> modifiers) {
        this.modifiers = modifiers;
    }

    /**
     * Load NAS configuraiton from a properties.
     * @param properties properties.
     */
    public void load(Properties properties) {
        name = properties.getProperty("nas.name");
        host = properties.getProperty("nas.host");
        sharedSecret = properties.getProperty("nas.shared-secret");
        version = properties.getProperty("nas.version");
        authType = properties.getProperty("nas.auth-type");
        range = properties.getProperty("nas.ipv4.range");
        port = Integer.parseInt(properties.getProperty("nas.port"));

        authenticateWithDomain = Boolean.parseBoolean(properties.getProperty("nas.authenticateWithDomain"));

        for (int i = 1; ; i++) {
            final String modifier = "nas.translation.modifier-" + i;
            final String pos = modifier + ".position",
                    tar = modifier + ".target",
                    val = modifier + ".value";

            final String position = properties.getProperty(pos),
                    target = properties.getProperty(tar),
                    value = properties.getProperty(val);

            if (StringUtils.isEmpty(position) ||
                    StringUtils.isEmpty(target) ||
                    StringUtils.isEmpty(value)) {
                break;
            }

            ModifierConfig mc = new ModifierConfig();
            mc.position = position;
            mc.target = target;
            mc.value = value;
            modifiers.add(mc);
        }
    }

    @Override
    public String toString() {
        return "NasConfig{" +
                "name='" + name + '\'' +
                ", authType='" + authType + '\'' +
                ", port=" + port +
                ", host='" + host + '\'' +
                ", sharedSecret='" + sharedSecret + '\'' +
                ", version='" + version + '\'' +
                ", range='" + range + '\'' +
                '}';
    }
}
