package cn.com.xinli.portal.persist;

import cn.com.xinli.portal.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcDaoSupport;
import org.springframework.stereotype.Repository;

/**
 * Project: portal
 *
 * @author zhoupeng 2015/12/7.
 */
@Repository
public class SessionDaoImp implements SessionDao {
    /** JDBC template. */
    @Autowired
    private NamedParameterJdbcDaoSupport jdbcDao;

    private JdbcTemplate getJdbcTemplate() {
        return jdbcDao.getJdbcTemplate();
    }

    @Override
    public Session get(String name) throws DataAccessException {
        JdbcTemplate template = getJdbcTemplate();
        return null;
    }

    @Override
    public void save(Session session) {

    }

    @Override
    public Session find(String user) {
        return null;
    }
}
