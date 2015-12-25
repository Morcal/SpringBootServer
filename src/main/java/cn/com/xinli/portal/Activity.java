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

    /** Severity. */
    enum Severity {
        FATAL,
        WARN,
        NORMAL,
        MINOR,
        VERBOSE
    }

    /** Activity action. */
    enum Action {
        AUTHENTICATE,
        CREATE_SESSION,
        GET_SESSION,
        UPDATE_SESSION,
        DELETE_SESSION
    }

    /**
     * Get id.
     * @return id.
     */
    long getId();

    /**
     * Get facility which activity occurred.
     * @return activity facility.
     */
    Facility getCategory();

    /**
     * Get activity severity.
     * @return activity severity.
     */
    Severity getSeverity();

    /**
     * Get remote information.
     * @return remote information.
     */
    String getRemote();

    /**
     * Get activity source information.
     * @return activity source information.
     */
    String getSource();

    /**
     * Get activity action.
     * @return activity action.
     */
    String getAction();

    /**
     * Get activity operation result.
     * @return activity operation result.
     */
    String getResult();

    /**
     * Get activity timestamp when it occurred.
     * @return activity timestamp when it occurred.
     */
    Date getTimestamp();
}
