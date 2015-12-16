package cn.com.xinli.portal.web;

import cn.com.xinli.portal.NasMapping;
import cn.com.xinli.portal.configuration.ConfigurationException;
import cn.com.xinli.portal.rest.RestResponse;
import cn.com.xinli.portal.rest.bean.Failure;
import cn.com.xinli.portal.util.AddressUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.InternalResourceView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


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
public class PwsWebController {
    /** Log. */
    private static final Log log = LogFactory.getLog(PwsWebController.class);

    @Autowired
    private NasMapping nasMapping;

    @Autowired
    private String pwsSchemeHeaderName;

    @Autowired
    private String pwsSchemeHeaderValue;

    @Autowired
    private String mainPageViewName;

    public View mainPageView() {
        return new InternalResourceView(mainPageViewName);
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
    @RequestMapping(value = "/${application}", method = RequestMethod.GET)
    public Object main(@RequestHeader(value="X-Real-Ip", defaultValue = "") String realIp,
                       @RequestParam(defaultValue = "") String sourceIp,
                       @RequestParam(defaultValue = "") String sourceMac,
                       @RequestParam(defaultValue = "") String nasIp,
                       @RequestParam(defaultValue = "") String basIp,
                       HttpServletRequest request) {
        /* TODO check logic here. */
        String deviceIp = StringUtils.isEmpty(nasIp) ? basIp : nasIp;

        while (true) {
            if (StringUtils.isEmpty(deviceIp)
                    || StringUtils.isEmpty(sourceIp)
                    || StringUtils.isEmpty(sourceMac)) {
                /* Invalid redirection, forward to main page. */
                break;
            }

            if (!AddressUtil.isValidateIp(realIp, sourceIp, request)) {
                break;
            }

            try {
                nasMapping.map(sourceIp, sourceMac, nasIp);
                log.debug("mapping {" + sourceIp + " " + sourceMac + "} -> {" + nasIp + "}");

            } catch (ConfigurationException e) {
                /* map failed, invalid nas ip, forward to main page. */
                if (log.isDebugEnabled()) {
                    log.debug("Invalid mapping, ", e);
                }
            }
        }

        return mainPageView();
    }

    @RequestMapping(value = "/${application}", method = RequestMethod.POST)
    public Object post() {
        return mainPageView();
    }

    @RequestMapping("/")
    public View main() {
        return mainPageView();
    }

    @RequestMapping("/redirect")
    public String redirect() {
        return "redirect:/html/redirect.html";
    }

    @ModelAttribute
    public void setPwsRestApiLocationHeader(HttpServletResponse response) {
        response.setHeader(pwsSchemeHeaderName, pwsSchemeHeaderValue);
    }

    @ExceptionHandler(value = { AuthenticationException.class, IOException.class})
    public ResponseEntity<Failure> handleExceptions() throws JsonProcessingException {
        Failure failure = new Failure();
        failure.setError(RestResponse.ERROR_SERVER_ERROR);
        return ResponseEntity.ok(failure);
    }
}
