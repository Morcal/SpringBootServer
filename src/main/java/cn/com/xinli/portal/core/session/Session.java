package cn.com.xinli.portal.core.session;

import cn.com.xinli.portal.core.certificate.Certificate;
import cn.com.xinli.portal.core.credentials.Credentials;
import cn.com.xinli.portal.core.nas.Nas;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.*;
import java.util.Date;

/**
 * PWS portal session.
 *
 * <p>This class instances represent portal authentication based
 * broadband internet connections.
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2015/12/2.
 */
@Entity
@PersistenceUnit(unitName = "bra")
@Table(schema = "PWS", name="session")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Session {
    /**
     * Auto generated id.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    /**
     * Session's host NAS/BRAS.
     */
    @ManyToOne
    @JoinColumn(name = "nas_id", referencedColumnName = "id")
    private Nas nas;

    /**
     * Session owner's credentials.
     */
    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "credentials_id")
    private Credentials credentials;

    @Column(name = "start_date")
    @JsonProperty("start_date")
    private Date startTime;

    /**
     * Session owner's client certificate.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "certificate_id", referencedColumnName = "id")
    private Certificate certificate;

    /** Last modified time (UNIX epoch time), do not save in database. */
    @Transient
    private long lastModified = 0L;

    /**
     * Get session internal id.
     *
     * @return session internal id.
     */
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Nas getNas() {
        return nas;
    }

    public void setNas(Nas nas) {
        this.nas = nas;
    }

    public Certificate getCertificate() {
        return certificate;
    }

    public void setCertificate(Certificate certificate) {
        this.certificate = certificate;
    }

    /**
     * Get Session start date.
     *
     * @return session start date.
     */
    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    /**
     * Get session last modified time (UNIX epoch time).
     * @return last modified time (UNIX epoch time).
     */
    public long getLastModified() {
        return lastModified;
    }

    public void setLastModified(long modified) {
        this.lastModified = modified;
    }

    public Credentials getCredentials() {
        return credentials;
    }

    public void setCredentials(Credentials credentials) {
        this.credentials = credentials;
    }

    public String cacheId() {
        return "session:" + id;
    }

    public static long fromCacheId(String id) {
        if (StringUtils.isEmpty(id)) {
            throw new IllegalArgumentException("cache id can not be blank.");
        }

        String value[] = id.split(":");
        if (value.length != 2) {
            throw new IllegalArgumentException("invalid session cache id.");
        }

        try {
            return Long.parseLong(value[1]);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("invalid session cache id.");
        }
    }

    /**
     * Create a paired information for ip and mac.
     *
     * <p>If mac is missing, "unknown" will be used.
     *
     * @param ip  ip address.
     * @param mac mac address.
     * @return paired information.
     */
    public static String pair(String ip, String mac) {
        return (ip + " " + (StringUtils.isEmpty(mac) ? "unknown" : mac)).trim();
    }

    /**
     * {@inheritDoc}
     *
     * <p>Call on this method will cause lazy-initialization. It may
     * fail due to no session.
     *
     * @return String presentation of session.
     */
    @Override
    public String toString() {
        return "Session{" +
                "id=" + id +
                ", nas=" + nas +
                ", credentials=" + credentials +
                ", startTime=" + startTime +
                ", certificate=" + certificate +
                ", lastModified=" + lastModified +
                '}';
    }
}
