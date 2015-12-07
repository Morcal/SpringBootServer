package cn.com.xinli.portal.rest.api;

import cn.com.xinli.portal.configuration.ConfigurationException;

import java.util.ArrayList;
import java.util.List;

/**
 * PWS api provider.
 *
 * Project: portal
 *
 * @author zhoupeng 2015/12/6.
 */
public class RestApiProvider {
    /** RestApi provider vendor. */
    private final String vendor;

    private final List<RestApiRegistration> registrations = new ArrayList<>();

    public RestApiProvider(String vendor) {
        this.vendor = vendor;
    }

    public String getVendor() {
        return vendor;
    }

    public List<RestApiRegistration> getRegistrations() {
        return registrations;
    }

    /**
     * Add a registration to provider.
     *
     * @param registration RestApi registration.
     * @throws ConfigurationException
     */
    public synchronized void addRegistration(RestApiRegistration registration)
            throws ConfigurationException {
        if (registrations.contains(registration)) {
            throw new ConfigurationException("Registration already exists.");
        }

        registrations.add(registration);
    }
}
