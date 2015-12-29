package cn.com.xinli.portal.configuration;

import org.apache.derby.jdbc.EmbeddedDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
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

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.io.File;

/**
 * Project: portal
 *
 * @author zhoupeng 2015/12/7.
 */
@Configuration
@EnableJpaRepositories(basePackages = "cn.com.xinli.portal.persist")
public class JpaConfiguration {
    /** Logger. */
    private final Logger logger = LoggerFactory.getLogger(JpaConfiguration.class);

    @Value("${pws.database.derby.scheme}") private String derbyScheme;

    @Value("${pws.database.derby.mem.enable}") private boolean enableDerbyMemDb;

    //@Bean
    public boolean firstRun() {
        if (enableDerbyMemDb) {
            /* Within derby memory database, always return true. */
            logger.info("> Running on derby memory database.");
            return true;
        } else {
            /* Check derby database directory to determine if we're on the first run. */
            File pws = new File(derbyScheme);
            return !(pws.exists() && pws.isDirectory());
        }
    }

    //@Bean(name = "datasource")
    public DataSource dataSource() {
        if (!enableDerbyMemDb) {
            DriverManagerDataSource dataSource = new DriverManagerDataSource();
            dataSource.setDriverClassName(EmbeddedDriver.class.getName());
            //dataSource.setUrl("jdbc:derby:PWS;create=true");
            String url = "jdbc:derby:PWS" + (firstRun() ? ";create=true" : "");
            dataSource.setUrl(url);
            return dataSource;
        } else {
            EmbeddedDatabaseBuilder builder = new EmbeddedDatabaseBuilder();
            return builder.setType(EmbeddedDatabaseType.DERBY)
                    .setName(derbyScheme)
                    .build();
        }
    }

    //@Autowired
    @Bean
    public PlatformTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
//        return new JpaTransactionManager(entityManagerFactory().getObject());
    }

    //@Bean(name = "jpa-vendor-adapter")
    public JpaVendorAdapter jpaVendorAdapter() {
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        vendorAdapter.setGenerateDdl(firstRun());
        vendorAdapter.setShowSql(true);
        return vendorAdapter;
    }

    //@Bean(name = "jpa-dialect")
    public JpaDialect jpaDialect() {
        HibernateJpaDialect dialect = new HibernateJpaDialect();
        dialect.setPrepareConnection(true);
        return dialect;
    }

    //@Bean(name = "jpa-exception-processor")
    @Bean
    public PersistenceExceptionTranslationPostProcessor exceptionProcessor() {
        return new PersistenceExceptionTranslationPostProcessor();
    }

    @Bean(name = "entityManagerFactory")
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
        factory.setDataSource(dataSource());
//        factory.setPersistenceUnitManager(persistenceUnitManager());
        factory.setJpaVendorAdapter(jpaVendorAdapter());
        factory.setJpaDialect(jpaDialect());
        //factory.setBeanFactory(beanFactory);
        factory.setPackagesToScan("cn.com.xinli.portal.persist");

        return factory;
    }
}
