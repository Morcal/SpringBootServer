package cn.com.xinli.portal.web.controller;

import cn.com.xinli.portal.core.configuration.ServerConfiguration;
import cn.com.xinli.portal.core.nas.Nas;
import cn.com.xinli.portal.core.nas.NasNotFoundException;
import cn.com.xinli.portal.core.nas.NasStore;
import cn.com.xinli.portal.core.runtime.NasStatistics;
import cn.com.xinli.portal.core.runtime.Runtime;
import cn.com.xinli.portal.core.session.Session;
import cn.com.xinli.portal.core.session.SessionStore;
import cn.com.xinli.portal.web.rest.*;
import org.apache.commons.lang3.StringUtils;
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

    @Autowired
    private NasStore nasStore;

    @Autowired
    private SessionStore sessionStore;

    @Autowired
    private ServerConfiguration serverConfiguration;

    @Autowired
    private Runtime runtime;

    /**
     * Search NAS devices.
     * @param query query string.
     * @return nas response.
     */
    @RequestMapping(value = "/nas", method = RequestMethod.POST)
    @ResponseBody
    @PreAuthorize("hasRole('ADMIN')")
    public NasResponse searchNas(@RequestParam(defaultValue = "") String query) {
        final Stream<Nas> stream;
        if (StringUtils.isEmpty(query)) {
            stream = nasStore.devices();
        } else {
            stream = nasStore.search(query);
        }

        return AdminResponseBuilders.nasResponseBuilder(stream).build();
    }

    /**
     * Retrieve a NAS device's information.
     * @param id nas id.
     * @return nas response.
     * @throws NasNotFoundException
     */
    @ResponseBody
    @RequestMapping(value = "/nas/{id}", method = RequestMethod.GET)
    @PreAuthorize("hasRole('ADMIN')")
    public NasResponse getNas(@PathVariable("id") Long id) throws NasNotFoundException {
        Nas nas = nasStore.get(id);
        return AdminResponseBuilders.nasResponseBuilder(Stream.of(nas)).build();
    }

    /**
     * Retrieve system configuration.
     * @return rest response.
     */
    @ResponseBody
    @RequestMapping(value = "/configuration", method = RequestMethod.GET)
    @PreAuthorize("hasRole('ADMIN')")
    public RestResponse serverConfiguration() {
        return AdminResponseBuilders.serverConfigurationResponseBuilder(serverConfiguration).build();
    }

    /**
     * Search sessions.
     * @param query query string.
     * @return session response.
     */
    @ResponseBody
    @RequestMapping(value = "/sessions", method = RequestMethod.POST)
    @PreAuthorize("hasRole('ADMIN')")
    public SessionsResponse searchSessions(@RequestParam(defaultValue = "") String query) {
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
}
