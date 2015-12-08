package cn.com.xinli.portal.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcDaoSupport;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

/**
 * Project: portal
 *
 * @author zhoupeng 2015/12/7.
 */
//@Configuration
public class DaoConfiguration {

    @Bean
    public JdbcDaoSupport jdbcDao() {
        DriverManagerDataSource datasource = new DriverManagerDataSource();
        datasource.setDriverClassName("org.apache.derby.EmbeddedDriver");
        datasource.setUrl("jdbc:derby:pws;create=true");
        JdbcDaoSupport dao = new NamedParameterJdbcDaoSupport();
        dao.setDataSource(datasource);
        return dao;
    }
}
