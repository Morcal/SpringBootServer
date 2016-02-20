package cn.com.xinli.portal.support.ehcache;

import cn.com.xinli.portal.core.session.Session;
import cn.com.xinli.portal.core.session.SessionManager;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sf.ehcache.event.CacheEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.Objects;

/**
 * Session Cache Event Listener.
 *
 * <p>This class listens on Session cache.
 * When a session is expired, this class will try to remove it in a future.
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2015/12/28.
 */
@Component
@Profile("standalone")
public class SessionCacheEventListener implements CacheEventListener, InitializingBean, Cloneable {
    /** Log. */
    private final Logger logger = LoggerFactory.getLogger(SessionCacheEventListener.class);

    @Autowired
    private SessionManager sessionManager;

    @Autowired
    private EhcacheSessionDataStore dataStore;

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(sessionManager);
    }

    @Override
    public void notifyElementRemoved(Ehcache ehcache, Element element) throws CacheException {
        logger.debug(">> Session removed from cache.");
    }

    @Override
    public void notifyElementPut(Ehcache ehcache, Element element) throws CacheException {
        logger.debug(">> Session put into cache.");
    }

    @Override
    public void notifyElementUpdated(Ehcache ehcache, Element element) throws CacheException {
        logger.debug(">> Session in cache updated.");
    }

    private void removeSessionElement(Element element) {
        Session session = dataStore.fromElement(element);
        Objects.requireNonNull(session, Session.EMPTY_SESSION);
        sessionManager.removeSessionInFuture(session);
    }

    @Override
    public void notifyElementExpired(Ehcache ehcache, Element element) {
        logger.debug(">> Session in cache expired.");
        removeSessionElement(element);
    }

    @Override
    public void notifyElementEvicted(Ehcache ehcache, Element element) {
        logger.debug(">> Session in cache evicted.");
        removeSessionElement(element);
    }

    @Override
    public void notifyRemoveAll(Ehcache ehcache) {
        logger.info("Session in cache all removed.");
    }

    @Override
    public void dispose() {

    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        super.clone();
        throw new CloneNotSupportedException("Session cache event listener clone not supported.");
    }
}
