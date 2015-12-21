package cn.com.xinli.portal.configuration;

import cn.com.xinli.portal.ServerConfig;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.derby.jdbc.EmbeddedDriver;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.orm.jpa.JpaDialect;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaDialect;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.io.File;

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

    @Autowired
    private ServerConfig serverConfig;

    @Bean
    public boolean firstRun() {
        if (serverConfig.useDerbyMemDb()) {
            /* Within derby memory database, always return true. */
            log.info("> Running on derby memory database.");
            return true;
        } else {
            /* Check derby database directory to determine if we're on the first run. */
            File pws = new File(serverConfig.getDerbyScheme());
            return !(pws.exists() && pws.isDirectory());
        }
    }

    @Bean
    public DataSource dataSource() {
        if (!serverConfig.useDerbyMemDb()) {
            DriverManagerDataSource dataSource = new DriverManagerDataSource();
            dataSource.setDriverClassName(EmbeddedDriver.class.getName());
            //dataSource.setUrl("jdbc:derby:PWS;create=true");
            String url = "jdbc:derby:PWS" + (firstRun() ? ";create=true" : "");
            dataSource.setUrl(url);
            return dataSource;
        } else {
            EmbeddedDatabaseBuilder builder = new EmbeddedDatabaseBuilder();
            return builder.setType(EmbeddedDatabaseType.DERBY)
                    .setName(serverConfig.getDerbyScheme())
                    .build();
        }
    }

    @Bean
    public PlatformTransactionManager transactionManager() {
        return new JpaTransactionManager(entityManagerFactory().getObject());
    }

    @Bean
    public JpaVendorAdapter jpaVendorAdapter() {
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        vendorAdapter.setGenerateDdl(firstRun());
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
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
        factory.setDataSource(dataSource());
//        factory.setPersistenceUnitManager(persistenceUnitManager());
        factory.setJpaVendorAdapter(jpaVendorAdapter());
        factory.setJpaDialect(jpaDialect());
        factory.setBeanFactory(beanFactory);
        factory.setPackagesToScan("cn.com.xinli.portal.persist");

        return factory;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }
}
