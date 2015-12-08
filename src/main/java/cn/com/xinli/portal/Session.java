package cn.com.xinli.portal;

import java.util.Date;

/**
 * Project: portal
 *
 * @author zhoupeng 2015/12/2.
 */
public interface Session {
    /**
     * Get Session start date.
     * @return session start date.
     */
    Date getStartDate();

    /**
     * Get session id.
     * @return session id.
     */
    long getId();
}
