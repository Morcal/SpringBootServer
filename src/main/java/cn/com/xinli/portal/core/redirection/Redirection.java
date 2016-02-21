package cn.com.xinli.portal.core.redirection;

import cn.com.xinli.portal.core.PortalError;
import cn.com.xinli.portal.core.RemoteException;
import org.apache.commons.lang3.StringUtils;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Redirection represents subset of {@link java.net.URI}.
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2016/2/21.
 */
public class Redirection {
    public static final String USER_IP = "redirect-user-ip";
    public static final String USER_MAC = "redirect-user-mac";
    public static final String NAS_IP = "redirect-nas-ip";

    private String scheme;
    private String host;
    private String port;
    private String path;
    private final Map<String, String> parameters;

    public Redirection() {
        parameters = new HashMap<>();
    }

    public String getScheme() {
        return scheme;
    }

    public void setScheme(String scheme) {
        this.scheme = scheme;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getParameter(String name) {
        return parameters.get(name);
    }

    private void setParameter(String exp) {
        if (!StringUtils.isEmpty(exp) && exp.contains("=")) {
            String[] values = exp.split("=");
            if (values.length > 1) {
                parameters.put(values[0], values[1]);
            }
        }
    }

    public void setParameter(String name, String value) {
        parameters.put(name, value);
    }

    /**
     * Parse a url string to a redirection.
     * @param url input url string.
     * @return redirection.
     * @throws RemoteException
     */
    public static Redirection parse(String url) throws RemoteException {
        if (StringUtils.isEmpty(url)) {
            throw new IllegalArgumentException("Url can not be empty.");
        }

        try {
            URI uri = new URI(url);
            String query = uri.getQuery();
            if (StringUtils.isEmpty(query)) {
                throw new RemoteException(PortalError.INVALID_REQUEST, "Invalid redirect url");
            }
            Redirection redirection = new Redirection();
            Arrays.stream(query.split("&")).forEach(redirection::setParameter);
            return redirection;
        } catch (URISyntaxException e) {
            throw new RemoteException(PortalError.INVALID_REQUEST, e.getMessage());
        }
    }

    @Override
    public String toString() {
        return "Redirection{" +
                "scheme='" + scheme + '\'' +
                ", host='" + host + '\'' +
                ", port='" + port + '\'' +
                ", path='" + path + '\'' +
                ", parameters=" + parameters +
                '}';
    }
}
