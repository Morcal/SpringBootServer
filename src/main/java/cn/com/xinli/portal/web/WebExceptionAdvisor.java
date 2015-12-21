package cn.com.xinli.portal.web;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.View;

/**
 * Project: xpws
 *
 * @author zhoupeng 2015/12/19.
 */
@ControllerAdvice(basePackages = "cn.com.xinli.portal.web")
public class WebExceptionAdvisor {
    /** Log. */
    private static final Log log = LogFactory.getLog(ExceptionHandler.class);

//    /** Default error view name. */
//    public static final String DEFAULT_ERROR_VIEW = "error";

    @Autowired
    private View defaultErrorView;

    @ExceptionHandler(value = { RuntimeException.class })
    public View handleRuntimeException(RuntimeException e) {
        if (log.isDebugEnabled()) {
            log.error(e);
        }

        return defaultErrorView;
    }
}
