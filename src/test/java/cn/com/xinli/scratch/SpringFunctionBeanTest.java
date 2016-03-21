package cn.com.xinli.scratch;

import org.junit.Assert;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
import org.junit.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.AnnotationConfigApplicationContext;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.stereotype.Service;
//
//import java.util.Random;
//import java.util.concurrent.atomic.AtomicInteger;
//
///**
// * <p>Project: xpws
// *
// * @author zhoupeng 2015/12/14.
// */
//@Configuration
//class TestConfiguration {
//    @Bean
//    public FoobarService foobarService() {
//        return new FoobarService();
//    }
//
//    @Bean
//    public ReferencingService referencingService() {
//        return new ReferencingService(foobarService());
//    }
//
//    @Bean
//    public RefService ref2Service() {
//        return new RefService(foobarService());
//    }
//}
//
//@Service
//class FoobarService {
//    /** Logger. */
//    private final Logger logger = LoggerFactory.getLogger(FoobarService.class);
//
//    Random random = new Random(System.currentTimeMillis());
//    public static AtomicInteger counter = new AtomicInteger(0);
//
//    public FoobarService() {
//        logger.warn(">>>> Creating foobar service.");
//        counter.incrementAndGet();
//    }
//
//    public int getValue() {
//        logger.debug("generating next integer.");
//        return random.nextInt();
//    }
//}
//
//@Service
//class ReferencingService {
//    final FoobarService foobarService;
//
//    @Autowired
//    public ReferencingService(FoobarService foobarService) {
//        this.foobarService = foobarService;
//    }
//
//    public int referenceIt() {
//        return foobarService.getValue();
//    }
//
//    public FoobarService getFoobarService() {
//        return foobarService;
//    }
//}
//
//@Service
//class RefService {
//    final FoobarService foobarService;
//
//    @Autowired
//    public RefService(FoobarService foobarService) {
//        this.foobarService = foobarService;
//    }
//}
//
//public class SpringFunctionBeanTest {
//    /** Logger. */
//    private final Logger logger = LoggerFactory.getLogger(SpringFunctionBeanTest.class);
//
//    @Test
//    public void testIt() {
//        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(TestConfiguration.class);
//        ReferencingService referencingService = context.getBean(ReferencingService.class);
//        int i = referencingService.referenceIt();
//
//        logger.debug("result: {}", i);
//
//        int references = FoobarService.counter.get();
//        logger.debug("reference: {}", references);
//
//        FoobarService another = context.getBean("foobarService", FoobarService.class);
//        FoobarService another2 = context.getBean("foobarService", FoobarService.class);
//        another.getValue();
//        another2.getValue();
//
//        referencingService.getFoobarService().getValue();
//
//
//        references = FoobarService.counter.get();
//        logger.debug("another reference: {}", references);
//        references = FoobarService.counter.get();
//        logger.debug("new reference: {}", references);
//
//        RefService ref = context.getBean(RefService.class);
//        Assert.assertNotNull(ref);
//        logger.debug("reference's service: {}", referencingService.foobarService);
//        logger.debug("ref's service: {}", ref.foobarService);
//    }
//}


public class SpringFunctionBeanTest {
    @Test
    public void testIt() {
        Assert.assertTrue(true);
    }
}