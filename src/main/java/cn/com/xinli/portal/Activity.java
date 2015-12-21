package cn.com.xinli.portal;

import java.util.Date;

/**
 * PWS activity.
 *
 * Project: xpws
 *
 * @author zhoupeng 2015/12/19.
 */
public interface Activity {
    /** System facilities. */
    enum Facility {
        /** System running PWS. */
        SYSTEM,
        /** PWS. */
        SERVER,
        /** Administration facilities. */
        ADMIN,
        /** Portal facilities. */
        PORTAL
    }

    enum Severity {
        FATAL,
        WARN,
        NORMAL,
        MINOR,
        VERBOSE
    }

    enum Action {
        AUTHENTICATE,
        CREATE_SESSION,
        GET_SESSION,
        UPDATE_SESSION,
        DELETE_SESSION
    }

    long getId();

    Facility getCategory();

    Severity getSeverity();

    String getRemote();

    String getSource();

    String getAction();

    String getResult();

    Date getTimestamp();
}
