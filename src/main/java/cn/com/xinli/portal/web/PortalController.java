package cn.com.xinli.portal.web;

import cn.com.xinli.portal.Constants;
import cn.com.xinli.portal.configuration.ApiProvider;
import cn.com.xinli.portal.configuration.ConfigurationException;
import cn.com.xinli.portal.configuration.NasMapping;
import cn.com.xinli.portal.util.SignatureUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

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

@RestController
@RequestMapping("/portal")
public class PortalController {

    @Autowired
    private NasMapping nasMapping;

    @Autowired
    private ApiProvider apiProvider;

    @Autowired
    private String privateKey;

    /**
     * Handle redirect.
     *
     * @param sourceIp source ip.
     * @param sourceMac source mac.
     * @param nasIp nas ip.
     * @param basIp bas ip.
     * @return springframework mvc result.
     */
    @RequestMapping(method = RequestMethod.GET)
    public Object redirect(@RequestHeader(value="X-Real-Ip") String realIp,
                           @RequestHeader(value="Authentication") String credential,
                           @RequestParam String sourceIp,
                           @RequestParam String sourceMac,
                           @RequestParam String nasIp,
                           @RequestParam String basIp,
                           @RequestParam String signature,
                           Map<String, Object> model,
                           HttpServletRequest request) {
        /* TODO check logic here. */
        String deviceIp = StringUtils.isEmpty(nasIp) ? basIp : nasIp;

        if (StringUtils.isEmpty(deviceIp)
                || StringUtils.isEmpty(sourceIp)
                || StringUtils.isEmpty(sourceMac)
                || StringUtils.isEmpty(signature)) {
            /* Invalid redirection, forward to main page. */
            return "main";
        }

        if (!isValidateIp(realIp, sourceIp, request)) {
            if (!verifySignature(credential, model)) {
                return "main";
            }
        }

        try {
            nasMapping.map(sourceIp, sourceMac, nasIp);
        } catch (ConfigurationException e) {
            /* map failed, invalid nas ip, forward to main page. */
            return "main";
        }

        return apiProvider;
    }

    /**
     * Verify incoming request's signature.
     * @param model HTTP request model.
     * @return true signature verified.
     */
    private boolean verifySignature(String credential, Map<String, Object> model) {
        StringBuilder builder = new StringBuilder();
        model.keySet().stream().sorted().forEach(key -> {
            String value = (String) model.get(key);
            try {
                builder.append(URLEncoder.encode(key, Constants.DEFAULT_CHAR_ENCODING))
                        .append("=")
                        .append(URLEncoder.encode(value, Constants.DEFAULT_CHAR_ENCODING))
                        .append(",");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        });

        if (builder.length() == 0) {
            return false;
        }

        /* Remove tail ','. */
        builder.deleteCharAt(builder.length() - 1);

        String signature = SignatureUtil.sign(builder.toString().getBytes(), privateKey, null);

        //FIXME
        return false;
    }

    /**
     * Check if incoming request ip is valid.
     * @param realIp nginx header real ip.
     * @param sourceIp source ip in parameters.
     * @param request HTTP request.
     * @return true valid.
     */
    private boolean isValidateIp(String realIp, String sourceIp, HttpServletRequest request) {
        String remoteAddr = request.getRemoteAddr();
        if (!StringUtils.isEmpty(realIp)) {
            /* nginx detected. */
            return StringUtils.equals(realIp, sourceIp);
        } else {
            return StringUtils.equals(remoteAddr, sourceIp);
        }
    }

    @RequestMapping(method = RequestMethod.POST)
    public Object post() {
        return "main";
    }
}
