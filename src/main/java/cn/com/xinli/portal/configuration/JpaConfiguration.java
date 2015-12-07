package cn.com.xinli.portal.configuration;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.derby.jdbc.EmbeddedDriver;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaDialect;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaDialect;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

/**
 * Project: portal
 *
 * @author zhoupeng 2015/12/7.
 */
@Configuration
public class JpaConfiguration implements BeanFactoryAware {
    /** Log. */
    private static final Log log = LogFactory.getLog(JpaConfiguration.class);

    /** Bean factory. */
    private BeanFactory beanFactory;

    @Bean
    private DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(EmbeddedDriver.class.getName());
        dataSource.setUrl("jdbc:derby:PWS;create=true");
        return dataSource;
    }

    @Bean
    private JpaTransactionManager transactionManager() {
       return new JpaTransactionManager(entityManagerFactory());
    }

    @Bean
    private JpaVendorAdapter jpaVendorAdapter() {
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        vendorAdapter.setGenerateDdl(true);
        vendorAdapter.setShowSql(true);
        return vendorAdapter;
    }

    @Bean
    private JpaDialect jpaDialect() {
        HibernateJpaDialect dialect = new HibernateJpaDialect();
        dialect.setPrepareConnection(true);
        return dialect;
    }

    @Bean
    EntityManagerFactory entityManagerFactory() {
        LocalContainerEntityManagerFactoryBean bean = new LocalContainerEntityManagerFactoryBean();
        bean.setDataSource(dataSource());
        //TODO create persistence unit manager.
        // bean.setPersistenceUnitManager();
        bean.setJpaVendorAdapter(jpaVendorAdapter());
        bean.setJpaDialect(jpaDialect());
        bean.setBeanFactory(beanFactory);
        //TODO create jpa property map.
        // bean.setJpaPropertyMap();
        return bean.getNativeEntityManagerFactory();
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        log.warn("setting bean factory.");
        this.beanFactory = beanFactory;
    }
}
