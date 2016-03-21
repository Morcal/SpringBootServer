package cn.com.xinli.portal.web.controller;

import cn.com.xinli.portal.core.nas.Nas;
import cn.com.xinli.portal.core.nas.NasNotFoundException;
import cn.com.xinli.portal.core.nas.NasStore;
import cn.com.xinli.portal.web.rest.AdminResponseBuilders;
import cn.com.xinli.portal.web.rest.NasResponse;
import cn.com.xinli.portal.web.rest.RestResponse;
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
}
