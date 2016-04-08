package cn.com.xinli.portal.web.controller;

import cn.com.xinli.portal.core.RemoteException;
import cn.com.xinli.portal.core.nas.Nas;
import cn.com.xinli.portal.core.nas.NasManager;
import cn.com.xinli.portal.core.nas.NasNotFoundException;
import cn.com.xinli.portal.core.nas.NasService;
import cn.com.xinli.portal.web.rest.AdminResponseBuilders;
import cn.com.xinli.portal.web.rest.NasResponse;
import cn.com.xinli.portal.web.rest.RestResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Stream;

/**
 * NAS Device administration controller.
 * @author zhoupeng, created on 2016/4/1.
 */
@Controller
@RequestMapping("/portal/admin/v1.0")
public class NasDeviceController {
    @Autowired
    private NasService nasService;

    @Autowired
    private NasManager nasManager;

    /**
     * Search NAS devices.
     * @param query query string.
     * @return nas response.
     */
    @RequestMapping(value = "/search/nas", method = RequestMethod.POST)
    @ResponseBody
    @PreAuthorize("hasRole('ADMIN')")
    public NasResponse searchNas(@RequestParam(defaultValue = "") String query) throws RemoteException {
        final Stream<Nas> stream;
        if (StringUtils.isEmpty(query)) {
            stream = nasService.all();
        } else {
            stream = nasService.search(query);
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
        Nas nas = nasService.get(id);
        return AdminResponseBuilders.nasResponseBuilder(Stream.of(nas)).build();
    }

    /**
     * Create NAS device.
     * @param nas nas device.
     * @return nas response.
     */
    @ResponseBody
    @RequestMapping(value = "/nas", method = RequestMethod.POST)
    @PreAuthorize("hasRole('ADMIN')")
    public NasResponse createNas(@RequestBody Nas nas) throws NasNotFoundException, RemoteException {
        Nas created = nasManager.create(nas);
        return AdminResponseBuilders.nasResponseBuilder(Stream.of(created)).build();
    }

    /**
     * Update nas device.
     * @param id nas device id.
     * @param nas nas.
     * @return nas response.
     * @throws NasNotFoundException
     */
    @ResponseBody
    @RequestMapping(value = "/nas/{id}", method = RequestMethod.POST)
    @PreAuthorize("hasRole('ADMIN')")
    public NasResponse updateNas(@PathVariable("id") long id,
                                 @RequestBody Nas nas) throws NasNotFoundException {
        Nas device = nasService.get(id);
        device.setIpv4Address(nas.getIpv4Address());
        device.setIpv6Address(nas.getIpv6Address());
        device.setName(nas.getName());

        nasService.save(device);

        return AdminResponseBuilders.nasResponseBuilder(Stream.of(device)).build();
    }

    /**
     * Delete a nas device.
     * @param id nas id to delete.
     * @return rest response.
     * @throws NasNotFoundException
     */
    @ResponseBody
    @RequestMapping(value = "/nas/{id}", method = RequestMethod.DELETE)
    @PreAuthorize("hasRole('ADMIN')")
    public RestResponse deleteNas(@PathVariable("id") long id) throws NasNotFoundException {
        nasManager.delete(id);
        return new RestResponse();
    }
}
