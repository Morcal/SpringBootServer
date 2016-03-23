package cn.com.xinli.portal.web.rest;

import cn.com.xinli.portal.core.session.Session;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Sessions response.
 * @author zhoupeng, created on 2016/3/23.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SessionsResponse extends RestResponse {
//    @JsonIgnore
//    private Stream<Session> stream;

    @JsonProperty
    private List<Session> sessions;

    @JsonProperty
    private long count;

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

//    public Stream<Session> getStream() {
//        return stream;
//    }
//
//    public void setStream(Stream<Session> stream) {
//        this.stream = stream;
//    }
//
//    public List<Session> getAll() {
//        return stream.collect(Collectors.toList());
//    }


    public List<Session> getSessions() {
        return sessions;
    }

    public void setSessions(List<Session> sessions) {
        sessions.size();
        this.sessions = sessions;
    }
}
