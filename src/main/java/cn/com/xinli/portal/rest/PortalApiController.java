package cn.com.xinli.portal.rest;

import cn.com.xinli.portal.rest.api.RestApiRegistration;
import cn.com.xinli.portal.rest.api.RestApi;
import cn.com.xinli.portal.rest.api.RestApiProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.Optional;

/**
 * Project: portal
 *
 * @author zhoupeng 2015/12/8.
 */
@Controller
public class PortalApiController {

    @Autowired
    private RestApiProvider restApiProvider;

    @RequestMapping(value = "/portal/{version}/{api}/{p1}/{p2}/{p3}", method = RequestMethod.GET)
    public Object handleApiGet(@RequestParam(value = "version") String version,
                            @RequestParam(value = "version") String api,
                            @RequestParam(value = "version") String p1,
                            @RequestParam(value = "version") String p2,
                            @RequestParam(value = "version") String p3,
                            Map<String, Object> model,
                            HttpServletRequest request) {
        Optional<RestApiRegistration> op = restApiProvider.getRegistrations().stream()
                .filter(reg -> reg.getVersion().equals(version))
                .findFirst();

        op.ifPresent(reg -> {
            Optional<RestApi> o = reg.getApis().stream()
                    .filter(a -> a.getAction().equals(api)
                            && a.getMethod().equals(RequestMethod.GET.name()))
                    .findFirst();

            o.ifPresent(target -> {

            });
        });

        if (!op.isPresent()) {
            return ErrorResponse.newBuilder()
                    .setError(ErrorResponse.INVALID_REQUEST).build();
        }

        //FIXME implementation missing.
        return "ok";
    }
}
