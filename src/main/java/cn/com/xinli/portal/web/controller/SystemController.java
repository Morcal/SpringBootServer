package cn.com.xinli.portal.web.controller;

import cn.com.xinli.portal.core.configuration.ServerConfiguration;
import cn.com.xinli.portal.core.nas.Nas;
import cn.com.xinli.portal.core.nas.NasNotFoundException;
import cn.com.xinli.portal.core.nas.NasStore;
import cn.com.xinli.portal.core.session.Session;
import cn.com.xinli.portal.core.session.SessionStore;
import cn.com.xinli.portal.web.rest.AdminResponseBuilders;
import cn.com.xinli.portal.web.rest.NasResponse;
import cn.com.xinli.portal.web.rest.RestResponse;
import cn.com.xinli.portal.web.rest.SessionsResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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

    @RequestMapping
    public ResponseEntity<RestResponse> summary() {
        //TODO implement summary.
        return null;
    }

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

    @ResponseBody
    @RequestMapping(value = "/nas/{id}", method = RequestMethod.GET)
    @PreAuthorize("hasRole('ADMIN')")
    public NasResponse getNas(@PathVariable("id") Long id) throws NasNotFoundException {
        Nas nas = nasStore.get(id);
        return AdminResponseBuilders.nasResponseBuilder(Stream.of(nas)).build();
    }

    @ResponseBody
    @RequestMapping(value = "/configuration", method = RequestMethod.GET)
    @PreAuthorize("hasRole('ADMIN')")
    public RestResponse serverConfiguration() {
        return AdminResponseBuilders.serverConfigurationResponseBuilder(serverConfiguration).build();
    }

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
}
