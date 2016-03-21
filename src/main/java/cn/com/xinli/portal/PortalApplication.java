package cn.com.xinli.portal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * Portal Spring-Boot Application.
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2015/11/30.
 */
@SpringBootApplication
@ComponentScan
public class PortalApplication {
    public static void main(String[] args) throws Exception {
        SpringApplication.run(PortalApplication.class, args);
    }
}
