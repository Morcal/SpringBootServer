package cn.com.xinli.portal.configuration;

import org.apache.derby.jdbc.EmbeddedDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.AdviceMode;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.io.File;

/**
 * PWS JPA configuration.
 * <p>
 * This class defines JPS configurations for package <value>"cn.com.xinli.portal.repository"</value>.
 * PWS enabled JPA transaction via spring "proxy target class" with {@link AdviceMode#PROXY}.
 * <p>
 * The underlying database is <em>Embedded</em> Apache Derby.
 * <p>
 * The underlying JPA provider is Hibernate.
 * <p>
 * All JPA support was provided by spring-data-jpa.
 * <p>
 * Project: xpws
 *
 * @author zhoupeng 2015/12/7.
 */
@Configuration
@EnableTransactionManagement(proxyTargetClass = true, mode = AdviceMode.PROXY)
@EnableJpaRepositories(basePackages = {"cn.com.xinli.portal.repository"})
public class JpaConfiguration {
    /** Logger. */
    private final Logger logger = LoggerFactory.getLogger(JpaConfiguration.class);

    @Value("${pws.database.derby.scheme}") private String derbyScheme;

    @Value("${pws.database.derby.mem.enable}") private boolean enableDerbyMemDb;

    /**
     * Check if system runs for the first time.
     * @return true if runs first time.
     */
    private boolean firstRun() {
        if (enableDerbyMemDb) {
            /* Within derby memory database, always return true. */
            logger.info("Running on derby memory database.");
            return true;
        } else {
            /* Check derby database directory to determine if we're on the first run. */
            File pws = new File(derbyScheme);
            return !(pws.exists() && pws.isDirectory());
        }
    }

    private JpaVendorAdapter jpaVendorAdapter() {
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        vendorAdapter.setGenerateDdl(firstRun());
        vendorAdapter.setShowSql(true);
        return vendorAdapter;
    }

    private JpaDialect jpaDialect() {
        HibernateJpaDialect dialect = new HibernateJpaDialect();
        dialect.setPrepareConnection(true);
        return dialect;
    }

    @Bean
    public DataSource dataSource() {
        if (!enableDerbyMemDb) {
            DriverManagerDataSource dataSource = new DriverManagerDataSource();
            dataSource.setDriverClassName(EmbeddedDriver.class.getName());
            //dataSource.setUrl("jdbc:derby:PWS;create=true");
            String url = "jdbc:derby:PWS" + (firstRun() ? ";create=true" : "");
            logger.info("derby url: {}", url);
            dataSource.setUrl(url);
            return dataSource;
        } else {
            logger.warn("+ Using memory derby!");
            EmbeddedDatabaseBuilder builder = new EmbeddedDatabaseBuilder();
            return builder.setType(EmbeddedDatabaseType.DERBY)
                    .setName(derbyScheme)
                    .build();
        }
    }

    @Bean
    public PlatformTransactionManager transactionManager() {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactory().getObject());
        return transactionManager;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
        factory.setDataSource(dataSource());
//        factory.setPersistenceUnitManager(persistenceUnitManager());
        factory.setJpaVendorAdapter(jpaVendorAdapter());
        factory.setJpaDialect(jpaDialect());
        //factory.setBeanFactory(beanFactory);
        factory.setPackagesToScan("cn.com.xinli.portal.core");

        return factory;
    }
}
