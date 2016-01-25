package cn.com.xinli.portal.core;

import org.apache.commons.lang3.StringUtils;

import javax.persistence.*;
import java.util.Date;

/**
 * PWS activity.
 *
 * Project: xpws
 *
 * @author zhoupeng 2015/12/19.
 */
@Entity
@PersistenceUnit(unitName = "system")
@Table(schema = "PWS", name="activity")
public class Activity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(nullable = false)
    private Facility facility;

    @Column(nullable = false)
    private Severity severity;

    @Column(nullable = false)
    private String remote;

    @Column(nullable = false)
    private String source;

    @Column(name = "source_info")
    private String sourceInfo;

    @Column(nullable = false)
    private String action;

    @Column
    private String result;

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
        WARN,
        NORMAL,
        MINOR,
        VERBOSE
    }

    /** System action. */
    public enum SystemAction {
        DELETE_OLD_ACTIVITIES,
        NTF_LOGOUT
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

        private String alias;

        SessionAction(String alias) {
            this.alias = alias;
        }

        public String alias() {
            return this.alias;
        }

        public static SessionAction ofAlias(String alias) {
            if (StringUtils.isEmpty(alias)) {
                return UNKNOWN;
            }

            for (SessionAction sessionAction : values()) {
                if (sessionAction.alias.equals(alias)) {
                    return sessionAction;
                }
            }
            return UNKNOWN;
        }
    }

}
