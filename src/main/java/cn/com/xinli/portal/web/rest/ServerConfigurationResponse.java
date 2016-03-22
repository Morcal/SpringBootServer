package cn.com.xinli.portal.web.rest;

import cn.com.xinli.portal.core.configuration.ServerConfiguration;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Server Configuration Response.
 * @author zhoupeng, created on 2016/3/23.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ServerConfigurationResponse extends RestResponse {
    @JsonProperty("server_configuration")
    private ServerConfiguration serverConfiguration;

    public ServerConfiguration getServerConfiguration() {
        return serverConfiguration;
    }

    public void setServerConfiguration(ServerConfiguration serverConfiguration) {
        this.serverConfiguration = serverConfiguration;
    }
}
