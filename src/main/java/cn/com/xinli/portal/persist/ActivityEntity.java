package cn.com.xinli.portal.persist;

import cn.com.xinli.portal.Activity;

import javax.persistence.*;
import java.util.Date;

/**
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

    @Column(nullable = false)
    private String action;

    @Column
    private String result;

    @Column(nullable = false)
    private Date timestamp;

    public void setAction(String action) {
        this.action = action;
    }

    public Facility getFacility() {
        return facility;
    }

    public void setFacility(Facility facility) {
        this.facility = facility;
    }

    public void setRemote(String remote) {
        this.remote = remote;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public void setSeverity(Severity severity) {
        this.severity = severity;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public Facility getCategory() {
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
    public String getAction() {
        return action;
    }

    @Override
    public String getResult() {
        return result;
    }

    @Override
    public Date getTimestamp() {
        return timestamp;
    }

    public void setId(long id) {
        this.id = id;
    }
}
