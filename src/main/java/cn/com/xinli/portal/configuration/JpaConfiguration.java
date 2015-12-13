package cn.com.xinli.portal.configuration;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.derby.jdbc.EmbeddedDriver;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.jpa.support.ClasspathScanningPersistenceUnitPostProcessor;
import org.springframework.data.jpa.support.MergingPersistenceUnitManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaDialect;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaDialect;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

/**
 * Project: portal
 *
 * @author zhoupeng 2015/12/7.
 */
@Configuration
@EnableJpaRepositories(basePackages = "cn.com.xinli.portal.persist")
public class JpaConfiguration implements BeanFactoryAware {
    /** Log. */
    private static final Log log = LogFactory.getLog(JpaConfiguration.class);

    /** Bean factory. */
    private BeanFactory beanFactory;

    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(EmbeddedDriver.class.getName());
        dataSource.setUrl("jdbc:derby:PWS;create=true");
        return dataSource;
    }

    @Bean
    public PlatformTransactionManager transactionManager(EntityManagerFactory factory) {
        return new JpaTransactionManager(factory);
    }

    @Bean
    public JpaVendorAdapter jpaVendorAdapter() {
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        vendorAdapter.setGenerateDdl(true);
        vendorAdapter.setShowSql(true);
        return vendorAdapter;
    }

    @Bean
    public JpaDialect jpaDialect() {
        HibernateJpaDialect dialect = new HibernateJpaDialect();
        dialect.setPrepareConnection(true);
        return dialect;
    }

    @Bean
    public BeanPostProcessor exceptionProcessor() {
        return new PersistenceExceptionTranslationPostProcessor();
    }

    @Bean
    public FactoryBean<EntityManagerFactory> entityManagerFactory() {
        LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
        factory.setDataSource(dataSource());
        /* Employee spring merging persistence unit manager. */
        MergingPersistenceUnitManager manager = new MergingPersistenceUnitManager();
//        manager.setPackagesToScan("cn.com.xinli.portal.persist");
//        factory.setPersistenceUnitManager(manager);
        factory.setPersistenceUnitName("portal");
        factory.setJpaVendorAdapter(jpaVendorAdapter());
        factory.setJpaDialect(jpaDialect());
        factory.setBeanFactory(beanFactory);
        factory.setPackagesToScan("cn.com.xinli.portal.persist");
        // TODO create jpa property map.
        // bean.setJpaPropertyMap();
        return factory;
    }

    @Bean
    public EntityManager entityManager(EntityManagerFactory factory) {
        return factory.createEntityManager();
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }
}
