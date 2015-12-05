package cn.com.xinli.portal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Project: portal
 *
 * @author zhoupeng 2015/11/30.
 */
@RestController
public class SampleController {

    private static final Log log = LogFactory.getLog(SampleController.class);
    @Autowired
    private HelloWorldService service;

    @RequestMapping("/hello")
    public Greeting getHelloMessage(@RequestParam(value="name", defaultValue="World") String name) {
        final String hello =  service.getHelloMessage();
        log.info(hello);
        final Greeting greeting = new Greeting(1, "hello");
        return new Greeting(1, "Hello");
    }
}
