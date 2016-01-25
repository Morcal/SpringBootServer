package cn.com.xinli.portal.web.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.View;

/**
 * Web exception advisor.
 *
 * <p>This class handles exceptions thrown from web controllers.
 * It returns an error view so that server will redirect result
 * page to an error page.
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2015/12/19.
 */
@ControllerAdvice(basePackages = "cn.com.xinli.portal.web")
public class WebExceptionAdvisor {
    /** Logger. */
    private final Logger logger = LoggerFactory.getLogger(WebExceptionAdvisor.class);

//    /** Default error view name. */
//    public static final String DEFAULT_ERROR_VIEW = "error";

    @Autowired
    private View defaultErrorView;

    @ExceptionHandler(value = { RuntimeException.class })
    public View handleRuntimeException(RuntimeException e) {
        if (logger.isDebugEnabled()) {
            logger.error("{} runtime exception", this.getClass(), e);
        }

        return defaultErrorView;
    }
}
