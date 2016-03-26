package cn.com.xinli.portal.web.rest;

import cn.com.xinli.portal.core.certificate.Certificate;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Certificate response.
 * @author zhoupeng, created on 2016/3/26.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CertificateResponse extends RestResponse {
    @JsonIgnore
    private Stream<Certificate> stream;

    public Stream<Certificate> getStream() {
        return stream;
    }

    public void setStream(Stream<Certificate> stream) {
        this.stream = stream;
    }

    @JsonProperty("certificates")
    public List<Certificate> all() {
        return stream.collect(Collectors.toList());
    }
}
