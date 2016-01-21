package cn.com.xinli.portal.controller;

import cn.com.xinli.portal.core.NasMapping;
import cn.com.xinli.portal.core.NasNotFoundException;
import cn.com.xinli.portal.support.rest.Scheme;
import cn.com.xinli.portal.util.AddressUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.View;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.StringJoiner;

/**
 * Portal web server root controller.
 *
 * This class defines the Web server main servlet.
 * NAS/BRAS devices should be configured to redirect unauthorized
 * web access to this servlet.
 *
 * Parameters should be past through the redirect URL, includes
 * NasIp/BasIp, SourceIp and SourceMac.
 *
 * This servlet need to build a {@link NasMapping}
 * using those parameters, so that subsequent portal requests can be
 * handled to the target device retrieving from the originate
 * {@link NasMapping}.
 *
 * <p>Potential Attack</p>
 * If users try to type redirection url to user-agents by
 * their own, then subsequent requests may fail due to
 * <ul>
 *     <li>invalid user-ip-mac-nas mapping (invalid nas ip).
 *     <li>wrong user-ip-mac-nas mapping (user not controlled by
 *     device which has given nas ip).</li>
 * </ul>
 * Under those circumstances, server may insert those wrong mappings.
 * If so, attackers can send wrong mapping request flood to PWS,
 * PWS will fail due to {@link OutOfMemoryError}, and attackers
 * can send wrong mapping request to mess with valid users.
 *
 * So, we should deal with two problems.
 * <ul>
 *     <li>wrong mapping.</li>
 *     <li>user-ip-mac-nas mapping storage.</li>
 * </ul>
 *
 * To solve the problems above, we setup two tests against clients
 * requests. First check if given user ip matches HTTP originate ip,
 * if not, deny those requests. If requests pass first test, then
 * check request signature to test if request already been tempered.
 * Ony requests pass two tests could be saved by PWS.
 *
 * Project: portal
 *
 * @author zhoupeng 2015/12/2.
 */
@Controller
public class WebController {
    /** Logger. */
    private final Logger logger = LoggerFactory.getLogger(WebController.class);

    @Autowired
    private NasMapping nasMapping;

    @Autowired
    private Scheme scheme;

    @Autowired
    private View mainPageView;

    /**
     * The PWS scheme header value.
     *
     * <p>Header string only supports ISO-8859-1 character set.
     * DO NOT try to set non-ASCII character inside header value string.
     * </p>
     * @param scheme application scheme.
     * @return scheme content string.
     */
    @Bean
    public String schemeHeaderValue(Scheme scheme) {
        StringJoiner joiner = new StringJoiner(";");
        joiner.add("version=" + scheme.getVersion())
                .add("apiuri=" + scheme.getUri())
                .add("server=" + scheme.getServer())
                .add("host=" + scheme.getHost())
                .add("scheme=" + scheme.getScheme())
                .add("port=" + String.valueOf(scheme.getPort()));
        return joiner.toString();
    }

    /**
     * Handle redirect.
     *
     * @param sourceIp source ip.
     * @param sourceMac source mac.
     * @param nasIp nas ip.
     * @param basIp bas ip.
     * @return springframework mvc result.
     */
    @RequestMapping(value = "/portal", method = RequestMethod.GET)
    public View main(@RequestHeader(value="X-Real-Ip", defaultValue = "") String realIp,
                       @RequestParam(value="source-ip", defaultValue = "") String sourceIp,
                       @RequestParam(value="source-mac", defaultValue = "") String sourceMac,
                       @RequestParam(value="nas-ip", defaultValue = "") String nasIp,
                       @RequestParam(value="basIp", defaultValue = "") String basIp,
                       HttpServletRequest request) {
        /* TODO check logic here. */
        String deviceIp = StringUtils.isEmpty(nasIp) ? basIp : nasIp;

        do {
            if (StringUtils.isEmpty(deviceIp)
                    || StringUtils.isEmpty(sourceIp)
                    || StringUtils.isEmpty(sourceMac)) {
                /* Invalid redirection, forward to main page. */
                break;
            }

            if (!AddressUtil.validateIp(realIp, sourceIp, request.getRemoteAddr())) {
                if (logger.isDebugEnabled()) {
                    logger.debug("invalid ip: {}.", sourceIp);
                }
                break;
            }

            try {
                nasMapping.map(sourceIp, sourceMac, nasIp);
            } catch (NasNotFoundException e) {
                logger.debug(" Nas not found, not mapped.");
            }

            if (logger.isDebugEnabled()) {
                logger.debug("mapping {{}, {}} -> {{}}.", sourceIp, sourceMac, nasIp);
            }
        } while (false);

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
