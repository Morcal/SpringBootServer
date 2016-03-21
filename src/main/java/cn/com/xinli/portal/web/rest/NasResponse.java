package cn.com.xinli.portal.web.rest;

import cn.com.xinli.portal.core.nas.Nas;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Nas device response.
 * @author zhoupeng, created on 2016/3/21.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NasResponse extends RestResponse {

    @JsonIgnore
    private Stream<Nas> stream;

    @JsonIgnore
    public Stream<Nas> getStream() {
        return stream;
    }

    public void setStream(Stream<Nas> stream) {
        this.stream = stream;
    }

    @JsonProperty("devices")
    public List<Nas> getDevices() {
        return stream.collect(Collectors.toList());
    }
}
