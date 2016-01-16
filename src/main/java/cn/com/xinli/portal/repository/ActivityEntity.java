package cn.com.xinli.portal.repository;

import cn.com.xinli.portal.admin.Activity;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.*;
import java.util.Date;

/**
 * Activity entity.
 *
 * Project: xpws
 *
 * @author zhoupeng 2015/12/17.
 */
@Entity
@PersistenceUnit(unitName = "system")
@Table(schema = "PWS", name="activity")
public class ActivityEntity implements Activity {
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

    @Override
    public long getId() {
        return id;
    }

    @Override
    public Facility getFacility() {
        return facility;
    }

    @Override
    public Severity getSeverity() {
        return severity;
    }

    @Override
    public String getRemote() {
        return remote;
    }

    @Override
    public String getSource() {
        return source;
    }

    @Override
    public String getSourceInfo() {
        return sourceInfo;
    }

    @Override
    public String getAction() {
        return action;
    }

    @Override
    public String getResult() {
        return result;
    }

    @Override
    public Date getCreated() {
        return created;
    }

    @Override
    public String toString() {
        return "ActivityEntity{" +
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
}
