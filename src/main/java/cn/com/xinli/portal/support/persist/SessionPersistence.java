package cn.com.xinli.portal.support.persist;

import cn.com.xinli.portal.core.session.Session;
import cn.com.xinli.portal.support.repository.SessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * Session persistence.
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2016/2/1.
 */
@Component
public class SessionPersistence {

    @Qualifier("sessionRepository")
    @Autowired
    private SessionRepository sessionRepository;

    public void all(Consumer<Session> consumer) {
        sessionRepository.findAll().forEach(consumer);
    }

    public Stream<Session> all() {
        return sessionRepository.findTop25ByIdNotNull();
    }

    public long count(String query) {
        return sessionRepository.count(query);
    }

    public Stream<Session> search(String query) {
        return sessionRepository.searchTop25(query);
    }

    public Session save(Session session) {
        return sessionRepository.save(session);
    }

    public void delete(Long id) {
        sessionRepository.delete(id);
    }

    public void delete(Session session) {
        sessionRepository.delete(session);
    }
}
