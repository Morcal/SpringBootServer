package cn.com.xinli.portal.web.rest;

import cn.com.xinli.portal.core.activity.Activity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Administration activity response.
 * @author zhoupeng, created on 2016/3/23.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ActivityResponse extends RestResponse {
    @JsonIgnore
    private Stream<Activity> stream;

    @JsonProperty
    private long count;

    public Stream<Activity> getStream() {
        return stream;
    }

    public void setStream(Stream<Activity> stream) {
        this.stream = stream;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    @JsonProperty("activities")
    public List<Activity> getAll() {
        return stream.collect(Collectors.toList());
    }
}
