package cn.com.xinli.portal.web.controller;

import cn.com.xinli.portal.core.certificate.Certificate;
import cn.com.xinli.portal.core.certificate.CertificateNotFoundException;
import cn.com.xinli.portal.core.certificate.CertificateService;
import cn.com.xinli.portal.web.rest.AdminResponseBuilders;
import cn.com.xinli.portal.web.rest.RestResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

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
    @RequestMapping(value = "/certificates", method = RequestMethod.POST)
    public RestResponse searchCertificates(String query) {
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
    public RestResponse get(@PathVariable("id") long id)
            throws CertificateNotFoundException {
        Certificate certificate = certificateService.get(id);

        return AdminResponseBuilders.certificateResponseBuilder(Stream.of(certificate))
                .build();
    }
}
