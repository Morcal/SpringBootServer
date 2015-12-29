package cn.com.xinli.portal;

import org.apache.commons.lang3.StringUtils;

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
        AUTHENTICATE("authorize"),
        CREATE_SESSION("connect"),
        GET_SESSION("get-session"),
        UPDATE_SESSION("update-session"),
        FIND_SESSION("find-session"),
        DELETE_SESSION("disconnect"),
        UNKNOWN("unknown");

        private String alias;

        Action(String alias) {
            this.alias = alias;
        }

        public String alias() {
            return this.alias;
        }

        public static Action ofAlias(String alias) {
            if (StringUtils.isEmpty(alias)) {
                return UNKNOWN;
            }

            for (Action action : values()) {
                if (action.alias.equals(alias)) {
                    return action;
                }
            }
            return UNKNOWN;
        }
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
