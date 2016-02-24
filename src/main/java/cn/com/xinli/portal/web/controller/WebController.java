package cn.com.xinli.portal.web.controller;

import cn.com.xinli.portal.core.nas.NasLocator;
import cn.com.xinli.portal.core.nas.NasNotFoundException;
import cn.com.xinli.portal.web.rest.Scheme;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.View;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.StringJoiner;

/**
 * Portal web server root controller.
 *
 * <p>This class defines the Web server main servlet.
 * NAS/BRAS devices should be configured to redirect unauthorized
 * web access to this servlet.
 *
 * <p>Parameters should be past through the redirect URL, includes
 * NasIp/BasIp, SourceIp and SourceMac.
 *
 * <p>This servlet need to build a mapping
 * using those parameters, so that subsequent portal requests can be
 * handled to the target device retrieving from the originate mapping.
 *
 * <p>Potential Attack</p>
 * If users try to type redirection url to user-agents by
 * their own, then subsequent requests may fail due to
 * <ul>
 *     <li>invalid user-ip-mac-nas mapping (invalid nas ip).
 *     <li>wrong user-ip-mac-nas mapping (user not controlled by
 *     device which has given nas ip).</li>
 * </ul>
 *
 * <p>Under those circumstances, server may insert those wrong mappings.
 * If so, attackers can send wrong mapping request flood to PWS,
 * PWS will fail due to {@link OutOfMemoryError}, and attackers
 * can send wrong mapping request to mess with valid users.
 *
 * <p>So, we should deal with two problems.
 * <ul>
 *     <li>wrong mapping.</li>
 *     <li>user-ip-mac-nas mapping storage.</li>
 * </ul>
 *
 * <p>To solve the problems above, we setup two tests against clients
 * requests. First check if given user ip matches HTTP originate ip,
 * if not, deny those requests. If requests pass first test, then
 * check request signature to test if request already been tempered.
 * Ony requests pass two tests could be saved by PWS.
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2015/12/2.
 */
@Controller
public class WebController {
    /** Logger. */
    private final Logger logger = LoggerFactory.getLogger(WebController.class);

    @Autowired
    private NasLocator nasLocator;

    @Autowired
    private Scheme scheme;

    @Autowired
    private View mainPageView;

    private static final String[] REDIRECT_USER_IP = {
            "userip", "user-ip", "user_ip", "user-ip-address", "user_ip_address", "wlanuserip",
            "sourceip", "source-ip", "source_ip"
    };

    private static final String[] REDIRECT_USER_MAC = {
            "usermac", "user-mac", "user_mac", "user-mac-address", "user_mac_address", "wlanusermac",
            "sourcemac", "source-mac", "source_mac"
    };

    private static final String[] REDIRECT_NAS_IP = {
            "nasip", "nas_ip", "nas-ip", "mscg", "basip", "bas-ip", "basip"
    };


    /**
     * The PWS scheme header value.
     *
     * <p>Header string only supports ISO-8859-1 character set.
     * DO NOT try to set non-ASCII character inside header value string.
     * @param scheme application scheme.
     * @return scheme content string.
     */
    private String schemeHeaderValue(Scheme scheme) {
        StringJoiner joiner = new StringJoiner(";");
        joiner.add("version=" + scheme.getVersion())
                .add("apiuri=" + scheme.getUri())
                .add("server=" + scheme.getServer())
                .add("host=" + scheme.getHost())
                .add("scheme=" + scheme.getScheme())
                .add("port=" + String.valueOf(scheme.getPort()));
        return joiner.toString();
    }

    String getParameter(String[] names, HttpServletRequest request) {
        for (String name : names) {
            String value = request.getParameter(name);
            if (value != null) {
                return value;
            }
        }

        return null;
    }

    @RequestMapping(value = "/portal", method = RequestMethod.GET)
    public View main(HttpServletRequest request) {
        final String sourceIp = getParameter(REDIRECT_USER_IP, request),
                sourceMac = getParameter(REDIRECT_USER_MAC, request),
                nasIp = getParameter(REDIRECT_NAS_IP, request);

        if (sourceIp != null && sourceMac != null && nasIp != null) {
            try {
                nasLocator.map(sourceIp, sourceMac, nasIp);
            } catch (NasNotFoundException e) {
                logger.debug(" Nas not found, not mapped.");
            }

            if (logger.isDebugEnabled()) {
                logger.debug("mapping {{}, {}} -> {{}}.", sourceIp, sourceMac, nasIp);
            }
        }

        return mainPageView;
    }

    @RequestMapping(value = "/portal", method = RequestMethod.POST)
    public View post() {
        return mainPageView;
    }

    @RequestMapping("/")
    public View main() {
        return mainPageView;
    }

    @ModelAttribute
    public void setPwsRestApiLocationHeader(HttpServletResponse response) {
        response.setHeader(scheme.getHeader(), schemeHeaderValue(scheme));
    }
}
