package cn.com.xinli.portal.support.rest;

import cn.com.xinli.portal.core.PortalError;
import cn.com.xinli.portal.core.ServerException;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * PWS api provider.
 *
 * <p>This class implements a REST API scheme.
 * A provider contains API {@link EntryPoint}s in {@link Registration}s.
 *
 * <p>Project: rest-api
 *
 * @author zhoupeng 2015/12/6.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Provider extends RestResponse {
    /** EntryPoint provider vendor. */
    @JsonProperty
    private String vendor;

    @JsonProperty
    private List<Registration> registrations = new ArrayList<>();

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    public String getVendor() {
        return vendor;
    }

    public void setRegistrations(List<Registration> registrations) {
        this.registrations = registrations;
    }

    public List<Registration> getRegistrations() {
        return registrations;
    }

    /**
     * Add a registration to provider.
     *
     * @param registration EntryPoint registration.
     * @throws ServerException
     */
    public synchronized void addRegistration(Registration registration)
            throws ServerException {
        if (registrations.contains(registration)) {
            throw new ServerException(PortalError.REDUNDANT_API_REGISTRATION);
        }

        registrations.add(registration);
    }

    /**
     * Find REST Api by method and uri.
     *
     * @param method http method.
     * @param uri    uri.
     * @return api entry point.
     */
    public Optional<EntryPoint> findApi(String method, String uri) {
        if (StringUtils.isEmpty(uri) || StringUtils.isEmpty(method)) {
            return Optional.empty();
        }

        for (Registration registration : registrations) {
            Optional<EntryPoint> entryPoint = registration.getApis().stream()
                    .filter(api -> api.getUrl().equals(uri) && api.getMethod().equalsIgnoreCase(method))
                    .findAny();
            if (entryPoint.isPresent()) {
                return entryPoint;
            }
        }

        return Optional.empty();
    }

    @Override
    public String toString() {
        return "Provider{" +
                "registrations=" + registrations +
                ", vendor='" + vendor + '\'' +
                '}';
    }
}
