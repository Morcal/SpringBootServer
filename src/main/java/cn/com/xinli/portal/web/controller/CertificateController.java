package cn.com.xinli.portal.web.controller;

import cn.com.xinli.portal.core.RemoteException;
import cn.com.xinli.portal.core.certificate.Certificate;
import cn.com.xinli.portal.core.certificate.CertificateNotFoundException;
import cn.com.xinli.portal.core.certificate.CertificateService;
import cn.com.xinli.portal.web.rest.AdminResponseBuilders;
import cn.com.xinli.portal.web.rest.RestResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Stream;

/**
 * Certificate controller.
 * @author zhoupeng, created on 2016/3/25.
 */
@Controller
@RequestMapping("/portal/admin/v1.0")
public class CertificateController {
    @Autowired
    private CertificateService certificateService;

    /**
     * Search certificates.
     * @param query query.
     * @return rest response.
     */
    @ResponseBody
    @RequestMapping(value = "/search/certificates", method = RequestMethod.POST)
    @PreAuthorize("hasRole('ADMIN')")
    public RestResponse searchCertificates(String query) throws RemoteException {
        final Stream<Certificate> stream;

        if (StringUtils.isEmpty(query)) {
            stream = certificateService.all();
        } else {
            stream = certificateService.search(query);
        }

        return AdminResponseBuilders.certificateResponseBuilder(stream).build();
    }

    /**
     * Retrieve certificate.
     * @param id certificate id.
     * @return rest response.
     * @throws CertificateNotFoundException
     */
    @ResponseBody
    @RequestMapping(value = "/certificates/{id}", method = RequestMethod.GET)
    @PreAuthorize("hasRole('ADMIN')")
    public RestResponse get(@PathVariable("id") long id)
            throws CertificateNotFoundException {
        Certificate certificate = certificateService.get(id);

        return AdminResponseBuilders.certificateResponseBuilder(Stream.of(certificate))
                .build();
    }

    /**
     * Create a new certificate.
     * @param appId application id.
     * @param vendor vendor.
     * @param os operating system name.
     * @param version version.
     * @return certificate response.
     */
    @ResponseBody
    @RequestMapping(value = "/certificates", method = RequestMethod.POST)
    @PreAuthorize("hasRole('ADMIN')")
    public RestResponse createCertificate(@RequestParam("app_id") String appId,
                                          @RequestParam("vendor") String vendor,
                                          @RequestParam("os") String os,
                                          @RequestParam("version") String version) {
        Certificate certificate = new Certificate();
        certificate.setAppId(appId);
        certificate.setDisabled(false);
        certificate.setOs(os);
        certificate.setVersion(version);
        certificate.setVendor(vendor);

        certificate = certificateService.create(certificate);

        return AdminResponseBuilders.certificateResponseBuilder(Stream.of(certificate))
                .build();
    }

    /**
     * Update certificate.
     * @param id certificate id.
     * @param certificate certificate.
     * @return certificate response.
     * @throws CertificateNotFoundException
     */
    @ResponseBody
    @RequestMapping(value = "/certificates/{id}", method = RequestMethod.POST)
    @PreAuthorize("hasRole('ADMIN')")
    public RestResponse updateCertificate(@PathVariable("id") long id,
                                          @RequestBody Certificate certificate)
            throws CertificateNotFoundException {
        Certificate c = certificateService.get(id);
        c.setAppId(certificate.getAppId());
        c.setDisabled(certificate.isDisabled());
        c.setOs(certificate.getOs());
        c.setVersion(certificate.getVersion());
        c.setVendor(certificate.getVendor());
        certificateService.save(c);

        return AdminResponseBuilders.certificateResponseBuilder(Stream.of(c))
                .build();
    }
}
