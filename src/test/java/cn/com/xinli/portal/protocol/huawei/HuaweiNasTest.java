package cn.com.xinli.portal.protocol.huawei;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;

/**
 * Project: xpws
 *
 * @author zhoupeng 2015/12/23.
 */

@Configuration
class TestConfiguration {
    @Bean
    public HuaweiNas nas() {
        return new HuaweiNas();
    }
}

@SpringBootApplication
class NasApplication {
    public static void main(String[] args) {
        SpringApplication.run(NasApplication.class, args);
    }
}

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = NasApplication.class)
public class HuaweiNasTest {
    @Autowired
    HuaweiNas nas;

    @Test
    public void testNas() throws IOException, InterruptedException {
        nas.start();
        Thread.sleep(10_000);
        nas.shutdown();
    }
}
