package cn.com.xinli.portal.scratch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Project: xpws
 *
 * @author zhoupeng 2015/12/14.
 */
@Configuration
class TestConfiguration {
    @Bean
    public FoobarService foobarService() {
        return new FoobarService();
    }

    @Bean
    public ReferencingService referencingService() {
        return new ReferencingService(foobarService());
    }

    @Bean
    public FoobarService anotherService() {
        return foobarService();
    }
}

@Service
class FoobarService {
    /** Logger. */
    private final Logger logger = LoggerFactory.getLogger(FoobarService.class);

    Random random = new Random(System.currentTimeMillis());
    public static AtomicInteger counter = new AtomicInteger(0);

    public FoobarService() {
        logger.warn(">>>> Creating foobar service.");
        counter.incrementAndGet();
    }

    public int getValue() {
        return random.nextInt();
    }
}

@Service
class ReferencingService {
    private final FoobarService foobarService;

    @Autowired
    public ReferencingService(FoobarService foobarService) {
        this.foobarService = foobarService;
    }

    public int referenceIt() {
        Assert.notNull(foobarService);
        return foobarService.getValue();
    }

    public FoobarService getFoobarService() {
        return foobarService;
    }
}

public class SpringFunctionBeanTest {
    /** Logger. */
    private final Logger logger = LoggerFactory.getLogger(SpringFunctionBeanTest.class);

    @Test
    public void testIt() {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(TestConfiguration.class);
        ReferencingService referencingService = context.getBean(ReferencingService.class);
        int i = referencingService.referenceIt();

        logger.debug("result: {}", i);

        int references = FoobarService.counter.get();
        logger.debug("reference: {}", references);

        FoobarService another = context.getBean("anotherService", FoobarService.class);
        another.getValue();

        references = FoobarService.counter.get();
        logger.debug("new reference: {}", references);
    }
}
