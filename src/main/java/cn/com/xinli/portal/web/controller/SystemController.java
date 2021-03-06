package cn.com.xinli.portal.web.controller;

import cn.com.xinli.portal.core.PortalError;
import cn.com.xinli.portal.core.PortalException;
import cn.com.xinli.portal.core.RemoteException;
import cn.com.xinli.portal.core.configuration.ServerConfigurationService;
import cn.com.xinli.portal.core.runtime.NasStatistics;
import cn.com.xinli.portal.core.runtime.Runtime;
import cn.com.xinli.portal.core.session.Session;
import cn.com.xinli.portal.core.session.SessionStore;
import cn.com.xinli.portal.web.rest.AdminResponseBuilders;
import cn.com.xinli.portal.web.rest.RestResponse;
import cn.com.xinli.portal.web.rest.SessionsResponse;
import cn.com.xinli.portal.web.rest.SystemStatisticsResponse;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * System administration controller.
 * <p>Project: xpws
 *
 * @author zhoupeng 2015/12/19.
 */
@RestController
@RequestMapping("/portal/admin/v1.0")
public class SystemController {
    /** Logger. */
    private final Logger logger = LoggerFactory.getLogger(SystemController.class);

    @Autowired
    private SessionStore sessionStore;

    @Autowired
    private ServerConfigurationService serverConfigurationService;

    @Autowired
    private Runtime runtime;

    /**
     * Retrieve system configuration.
     * @return rest response.
     */
    @ResponseBody
    @RequestMapping(value = "/configuration", method = RequestMethod.GET)
    @PreAuthorize("hasRole('ADMIN')")
    public RestResponse serverConfiguration() {
        return AdminResponseBuilders.serverConfigurationResponseBuilder(
                serverConfigurationService.getServerConfiguration()).build();
    }

    /**
     * Search sessions.
     * @param query query string.
     * @return session response.
     */
    @ResponseBody
    @RequestMapping(value = "/sessions", method = RequestMethod.POST)
    @PreAuthorize("hasRole('ADMIN')")
    public SessionsResponse searchSessions(@RequestParam(defaultValue = "") String query) throws RemoteException {
        final Stream<Session> stream;
        final long count;

        if (!StringUtils.isEmpty(query)) {
            count = sessionStore.count(query);
            stream = sessionStore.search(query);
        } else {
            count = sessionStore.count();
            stream = sessionStore.all();
        }

        return AdminResponseBuilders.sessionsResponseBuilder()
                .setStream(stream)
                .setCount(count)
                .build();
    }

    /**
     * Get system runtime statistics.
     * @return system statistics.
     */
    @ResponseBody
    @RequestMapping(value = "/statistics", method = RequestMethod.GET)
    @PreAuthorize("hasRole('ADMIN')")
    public SystemStatisticsResponse systemStatistics() {
        List<NasStatistics> devices =
            runtime.getNasStatisticsMap().values().stream().collect(Collectors.toList());

        return AdminResponseBuilders.systemStatisticsBuilder()
                .setLoad(runtime.getLoadStatistics())
                .setSession(runtime.getSessionStatistics())
                .setDevices(devices)
                .setTotal(runtime.getTotalSessionStatistics())
                .build();
    }

    /**
     * Configure server configuration.
     * @param key server configuration key.
     * @param value server configuration value.
     * @return rest response.
     * @throws PortalException
     */
    @ResponseBody
    @RequestMapping(value = "/configuration", method = RequestMethod.POST)
    @PreAuthorize("hasRole('ADMIN')")
    public RestResponse configure(@RequestParam("key") String key,
                                  @RequestParam("value") String value) throws PortalException {
        if (StringUtils.isEmpty(key)) {
            throw new RemoteException(PortalError.INVALID_REQUEST);
        }

        if (logger.isTraceEnabled()) {
            logger.trace("update configuration {}:{}", key, value);
        }

        serverConfigurationService.updateConfigurationEntry(key, value);

        return AdminResponseBuilders.successResponseBuilder().build();
    }
}
