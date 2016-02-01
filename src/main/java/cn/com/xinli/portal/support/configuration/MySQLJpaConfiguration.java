package cn.com.xinli.portal.support.configuration;

import org.springframework.boot.orm.jpa.EntityScan;
import org.springframework.context.annotation.AdviceMode;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

/**
 * PWS JPA configuration.
 *
 * <p>This class defines JPS configurations for package <value>"cn.com.xinli.portal.support.repository"</value>.
 * PWS enabled JPA transaction via spring "proxy target class" with {@link AdviceMode#PROXY}.
 *
 * <p>The underlying database is <em>Embedded</em> Apache Derby.
 *
 * <p>The underlying JPA provider is Hibernate.
 *
 * <p>All JPA support was provided by spring-data-jpa.
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2015/12/7.
 */
@Configuration
@EnableTransactionManagement(proxyTargetClass = true, mode = AdviceMode.PROXY)
@EnableJpaRepositories(basePackages = {"cn.com.xinli.portal.support.repository"})
@EntityScan(basePackages = {"cn.com.xinli.portal.core"})
@Profile("mysql")
public class MySQLJpaConfiguration {
    /** Logger. */
//    private final Logger logger = LoggerFactory.getLogger(MySQLJpaConfiguration.class);

    @Bean
    public DataSource dataSource() {
        throw new UnsupportedOperationException("MySQL not supported yet.");
    }

//    @Bean
//    public JpaVendorAdapter getJpaVendorAdapter() {
//        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
//        vendorAdapter.setGenerateDdl(false/*firstRun()*/);
//        vendorAdapter.setShowSql(true);
//        return vendorAdapter;
//    }

//    JpaDialect jpaDialect() {
//        HibernateJpaDialect dialect = new HibernateJpaDialect();
//        dialect.setPrepareConnection(true);
//        return dialect;
//    }


//    @Bean
//    public PlatformTransactionManager transactionManager() {
//        JpaTransactionManager transactionManager = new JpaTransactionManager();
//        transactionManager.setEntityManagerFactory(entityManagerFactory().getObject());
//        return transactionManager;
//    }
//
//    @Bean
//    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
//        LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
//        factory.setDataSource(getDataSource());
//        //factory.setPersistenceUnitManager(persistenceUnitManager());
//        factory.setJpaVendorAdapter(getJpaVendorAdapter());
//        factory.setJpaDialect(jpaDialect());
//        //factory.setBeanFactory(beanFactory);
//        factory.setPackagesToScan("cn.com.xinli.portal.core");
//
//        return factory;
//    }
}
