package cn.com.xinli.portal.web.rest;

import cn.com.xinli.portal.core.runtime.LoadStatistics;
import cn.com.xinli.portal.core.runtime.NasStatistics;
import cn.com.xinli.portal.core.runtime.SessionStatistics;
import cn.com.xinli.portal.core.runtime.TotalSessionStatistics;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * System statistics response.
 * @author zhoupeng, created on 2016/3/28.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SystemStatisticsResponse extends RestResponse {
    @JsonProperty("load")
    private LoadStatistics loadStatistics;

    @JsonProperty("devices")
    private List<NasStatistics> nasStatistics;

    @JsonProperty("session")
    private SessionStatistics sessionStatistics;

    @JsonProperty("total")
    private TotalSessionStatistics totalSessionStatistics;

    public LoadStatistics getLoadStatistics() {
        return loadStatistics;
    }

    public void setLoadStatistics(LoadStatistics loadStatistics) {
        this.loadStatistics = loadStatistics;
    }

    public SessionStatistics getSessionStatistics() {
        return sessionStatistics;
    }

    public void setSessionStatistics(SessionStatistics sessionStatistics) {
        this.sessionStatistics = sessionStatistics;
    }

    public List<NasStatistics> getNasStatistics() {
        return nasStatistics;
    }

    public void setNasStatistics(List<NasStatistics> nasStatistics) {
        this.nasStatistics = nasStatistics;
    }

    public TotalSessionStatistics getTotalSessionStatistics() {
        return totalSessionStatistics;
    }

    public void setTotalSessionStatistics(TotalSessionStatistics totalSessionStatistics) {
        this.totalSessionStatistics = totalSessionStatistics;
    }
}
