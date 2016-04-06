package cn.com.xinli.portal.core.configuration;

import cn.com.xinli.portal.core.PortalError;
import cn.com.xinli.portal.core.ServerException;

/**
 * Server configuration not exists exception.
 * @author zhoupeng, created on 2016/3/31.
 */
public class ServerConfigurationNotExistsException extends ServerException {
    public ServerConfigurationNotExistsException(String entry) {
        super(PortalError.INVALID_SERVER_CONFIGURATION_ENTRY, entry);
    }
}
