package cn.com.xinli.portal.core.activity;

import cn.com.xinli.portal.core.PortalError;
import cn.com.xinli.portal.core.ServerException;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.*;
import java.util.Date;

/**
 * PWS activity.
 *
 * <p>This class represents auditing information for system.
 * Any portal business (requests and responses) will be saved
 * as an auditing-logging entry.
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2015/12/19.
 */
@Entity
@PersistenceUnit(unitName = "system")
@Table(schema = "PWS", name="activity")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Activity {
    public static final String EMPTY_ACTIVITY = "Activity is empty.";
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonProperty
    private long id;

    @JsonProperty
    @Column(nullable = false)
    private Facility facility;

    @JsonProperty
    @Column(nullable = false)
    private Severity severity;

    @JsonProperty
    @Column(nullable = false)
    private String remote;

    @JsonProperty
    @Column(nullable = false)
    private String source;

    @JsonProperty("source_info")
    @Column(name = "source_info")
    private String sourceInfo;

    @JsonProperty
    @Column(nullable = false)
    private String action;

    @JsonProperty
    @Column
    private String result;

    @JsonProperty
    @Column(nullable = false)
    private Date created;

    public void setId(long id) {
        this.id = id;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public void setFacility(Facility facility) {
        this.facility = facility;
    }

    public void setRemote(String remote) {
        this.remote = remote;
    }

    public void setResult(String result) {
        this.result = StringUtils.left(result, 254);
    }

    public void setSeverity(Severity severity) {
        this.severity = severity;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public void setSourceInfo(String sourceInfo) {
        this.sourceInfo = StringUtils.left(sourceInfo, 254);
    }

    /**
     * Get id.
     * @return id.
     */
    public long getId() {
        return id;
    }

    /**
     * Get facility which activity occurred.
     * @return activity facility.
     */
    public Facility getFacility() {
        return facility;
    }

    /**
     * Get activity severity.
     * @return activity severity.
     */
    public Severity getSeverity() {
        return severity;
    }

    /**
     * Get remote information.
     * @return remote information.
     */
    public String getRemote() {
        return remote;
    }

    /**
     * Get activity source information.
     * @return activity source information.
     */
    public String getSource() {
        return source;
    }

    /**
     * Get source information.
     * @return source information.
     */
    public String getSourceInfo() {
        return sourceInfo;
    }

    /**
     * Get activity sessionAction.
     * @return activity sessionAction.
     */
    public String getAction() {
        return action;
    }

    /**
     * Get activity operation result.
     * @return activity operation result.
     */
    public String getResult() {
        return result;
    }

    /**
     * Get activity timestamp when it occurred.
     * @return activity timestamp when it occurred.
     */
    public Date getCreated() {
        return created;
    }

    @Override
    public String toString() {
        return "Activity{" +
                "action='" + action + '\'' +
                ", id=" + id +
                ", facility=" + facility +
                ", severity=" + severity +
                ", remote='" + remote + '\'' +
                ", source='" + source + '\'' +
                ", sourceInfo='" + sourceInfo + '\'' +
                ", result='" + result + '\'' +
                ", created=" + created +
                '}';
    }

    /** System facilities. */
    public enum Facility {
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
    public enum Severity {
        FATAL,
        ERROR,
        WARN,
        INFO,
        DEBUG,
        TRACE
    }

    /** System action. */
    public enum SystemAction {
        DELETE_OLD_ACTIVITIES,
        NTF_LOGOUT
    }

    public enum AdminAction {
        AUTHORIZE("authorize"),
        LOGIN("login"),
        CONFIGURE("configure");

        /**
         * Action name alias.
         * <p>Alias may be used in system modules like REST APIs.
         */
        private String alias;

        AdminAction(String alias) {
            this.alias = alias;
        }

        public String alias() {
            return this.alias;
        }

    }

    public enum ActivityAction {
        GET("get-activity"),
        SEARCH("search-activity");

        /**
         * Action name alias.
         * <p>Alias may be used in system modules like REST APIs.
         */
        private String alias;

        ActivityAction(String alias) {
            this.alias = alias;
        }

        public String alias() {
            return this.alias;
        }
    }

    /** Activity sessionAction. */
    public enum NasAction {
        SEARCH("search-nas"),
        GET("get-nas"),
        ADD("add-nas"),
        UPDATE("update-nas"),
        ENABLE("enable-nas"),
        DISABLE("update-nas"),
        DELETE("delete-nas"),
        UNKNOWN("unknown");

        /**
         * Action name alias.
         * <p>Alias may be used in system modules like REST APIs.
         */
        private String alias;

        NasAction(String alias) {
            this.alias = alias;
        }

        public String alias() {
            return this.alias;
        }
    }

    /** Activity sessionAction. */
    public enum SessionAction {
        AUTHENTICATE("authorize"),
        CREATE_SESSION("connect"),
        GET_SESSION("get-session"),
        UPDATE_SESSION("update-session"),
        FIND_SESSION("find-session"),
        DELETE_SESSION("disconnect"),
        UNKNOWN("unknown");

        /**
         * Action name alias.
         * <p>Alias may be used in system modules like REST APIs.
         */
        private String alias;

        SessionAction(String alias) {
            this.alias = alias;
        }

        public String alias() {
            return this.alias;
        }

        /**
         * Get Session action with alias name.
         * @param alias alias name.
         * @return Session action.
         * @throws IllegalArgumentException if given alias is blank.
         * @throws ServerException if given alias is invalid.
         */
        public static SessionAction of(String alias) throws ServerException {
            if (StringUtils.isEmpty(alias)) {
                throw new IllegalArgumentException("Session action alias can not be blank");
            }

            for (SessionAction sessionAction : values()) {
                if (sessionAction.alias.equals(alias)) {
                    return sessionAction;
                }
            }

            throw new ServerException(PortalError.ACTIVITY_ACTION_ERROR, "alias:" + alias);
        }
    }

}
