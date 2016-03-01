package cn.com.xinli.portal.support;

import cn.com.xinli.portal.core.PortalError;
import cn.com.xinli.portal.core.RemoteException;
import cn.com.xinli.portal.core.configuration.RedirectConfiguration;
import cn.com.xinli.portal.core.configuration.ServerConfiguration;
import cn.com.xinli.portal.core.redirection.RedirectService;
import cn.com.xinli.portal.core.redirection.Redirection;
import cn.com.xinli.portal.util.AddressUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Redirect Service Support.
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2016/2/21.
 */
@Service
public class RedirectServiceSupport implements RedirectService {
    /** Logger. */
    private final Logger logger = LoggerFactory.getLogger(RedirectServiceSupport.class);

    @Autowired
    private ServerConfiguration serverConfiguration;

    String getParameter(Redirection redirection, String[] names) {
        for (String name : names) {
            String value = redirection.getParameter(name.trim());
            if (value != null) {
                return value;
            }
        }
        return null;
    }

    @Override
    public Redirection verify(Redirection redirection, String ip, String mac) throws RemoteException {
        RedirectConfiguration configuration = serverConfiguration.getRedirectConfiguration();
        String userIp = getParameter(redirection, configuration.getUserIp()),
                userMac = getParameter(redirection, configuration.getUserMac()),
                nasIp = getParameter(redirection, configuration.getNasIp());

        if (logger.isTraceEnabled()) {
            logger.trace("redirection ip: {}, mac: {}, nasIp: {}",
                    userIp, userMac, nasIp);
        }

        userMac = StringUtils.isEmpty(userMac) ? "" : AddressUtil.formatMac(userMac);

        if (StringUtils.isEmpty(userIp)) {
            throw new RemoteException(PortalError.INVALID_REQUEST, "redirect url user ip missing");
        }

        if (!StringUtils.equals(userIp, ip) /*||
                (!StringUtils.isEmpty(userMac) && !StringUtils.isEmpty(mac) &&
                !StringUtils.equals(userMac, AddressUtil.formatMac(mac)))*/) {
            throw new RemoteException(PortalError.NAT_NOT_ALLOWED);
        }

        Redirection rs = new Redirection();
        rs.setParameter(Redirection.USER_IP, ip);
        rs.setParameter(Redirection.USER_MAC, mac);
        rs.setParameter(Redirection.NAS_IP, nasIp);

        return rs;
    }

}