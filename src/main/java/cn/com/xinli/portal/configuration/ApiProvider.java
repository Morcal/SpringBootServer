package cn.com.xinli.portal.configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * PWS api provider.
 *
 * Project: portal
 *
 * @author zhoupeng 2015/12/6.
 */
public class ApiProvider {
    /** Api provider vendor. */
    private final String vendor;

    private final List<ApiRegistration> registrations = new ArrayList<>();

    public ApiProvider(String vendor) {
        this.vendor = vendor;
    }

    public String getVendor() {
        return vendor;
    }

    public List<ApiRegistration> getRegistrations() {
        return registrations;
    }

    /**
     * Add a registration to provider.
     *
     * @param registration Api registration.
     * @throws ConfigurationException
     */
    public synchronized void addRegistration(ApiRegistration registration)
            throws ConfigurationException {
        if (registrations.contains(registration)) {
            throw new ConfigurationException("Registration already exists.");
        }

        registrations.add(registration);
    }
}
