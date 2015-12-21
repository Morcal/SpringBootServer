package cn.com.xinli.portal.rest;

import cn.com.xinli.portal.rest.api.Provider;
import cn.com.xinli.portal.rest.bean.Failure;
import cn.com.xinli.portal.rest.configuration.ApiConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Project: xpws
 *
 * @author zhoupeng 2015/12/15.
 */
@Controller
@RequestMapping("/${pws.root}/" + ApiConfiguration.API_PATH)
public class ApiController {

    @Autowired
    private Provider restApiProvider;

    @RequestMapping
    public ResponseEntity<Provider> api() {
        return ResponseEntity.ok(restApiProvider);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Failure> handleAuthenticationException(AuthenticationException e) {
        Failure failure = new Failure();
        failure.setError(RestResponse.ERROR_UNAUTHORIZED_REQUEST);
        return ResponseEntity.ok(failure);
    }
}
