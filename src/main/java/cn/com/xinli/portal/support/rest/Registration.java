package cn.com.xinli.portal.support.rest;

import cn.com.xinli.portal.core.PortalError;
import cn.com.xinli.portal.core.ServerException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * EntryPoint registration.
 *
 * Project: rest-api
 *
 * @author zhoupeng 2015/12/6.
 */
public class Registration {
    /** EntryPoint type. */
    private String type;

    /** EntryPoint version. */
    private String version;

    /** APIs this registration provides. */
    private List<EntryPoint> apis;

    /** Default constructor for JSON parser. */
    public Registration() {
        this.type = "";
        this.version = "";
        this.apis = Collections.emptyList();
    }

    public Registration(String type, String version) {
        this.type = type;
        this.version = version;
        this.apis = new ArrayList<>();
    }

    public void setApis(List<EntryPoint> apis) {
        this.apis = apis;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    /**
     * Get EntryPoint type.
     * @return EntryPoint type.
     */
    public String getType() {
        return type;
    }

    /**
     * Get EntryPoint version.
     * @return EntryPoint version.
     */
    public String getVersion() {
        return version;
    }

    /**
     * Get all APIs.
     * @return EntryPoint list.
     */
    public List<EntryPoint> getApis() {
        return apis;
    }

    /**
     * Register a new EntryPoint.
     * @param api EntryPoint.
     * @throws ServerException
     */
    public synchronized EntryPoint registerApi(EntryPoint api) throws ServerException {
        if (apis.contains(api)) {
            throw new ServerException(
                    PortalError.of("redundant_api_entry"),
                    "api: " + api.toString() + " already registered");
        }
        apis.add(api);
        return api;
    }

    @Override
    public String toString() {
        return "Registration{" +
                "apis=" + apis +
                ", type='" + type + '\'' +
                ", version='" + version + '\'' +
                '}';
    }
}
