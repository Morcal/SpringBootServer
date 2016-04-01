package cn.com.xinli.portal.core.credentials;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;
import java.util.List;

/**
 * Credentials Translation.
 *
 * <p>Translate credentials when necessary.
 * Classes implement this interface should not modify original credentials,
 * instead of modifying, implement classes should return a new credentials
 * which was translated from original credentials.
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2016/1/1.
 */
@Entity
@PersistenceUnit(unitName = "system")
@Table(schema = "PWS", name="credentials_translation")
@JsonInclude
public class CredentialsTranslation {
    /** Internal translation id. */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonProperty
    private long id;

    /** Associated foreign modifiers. */
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "translation_modifier", schema = "PWS",
            joinColumns = @JoinColumn(name = "trans_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "mod_id", referencedColumnName = "id"))
    @JsonProperty
    private List<CredentialsModifier> modifiers;

    /** Associated credentials encoder. */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "encoder_id", referencedColumnName = "id")
    @JsonProperty
    private CredentialsEncoder encoder;

    /** Optional encoder additional value. */
    @Column(name = "encoder_value")
    @JsonProperty("encoder_value")
    private String encoderAdditional;

    /** NAS truncate domain when authenticate or not. */
    @Column(name ="authenticate_with_domain", nullable = false)
    @JsonProperty("authenticate_with_domain")
    private boolean authenticateWithDomain;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public List<CredentialsModifier> getModifiers() {
        return modifiers;
    }

    public void setModifiers(List<CredentialsModifier> modifiers) {
        this.modifiers = modifiers;
    }

    public CredentialsEncoder getEncoder() {
        return encoder;
    }

    public void setEncoder(CredentialsEncoder encoder) {
        this.encoder = encoder;
    }

    public String getEncoderAdditional() {
        return encoderAdditional;
    }

    public void setEncoderAdditional(String encoderAdditional) {
        this.encoderAdditional = encoderAdditional;
    }

    public boolean isAuthenticateWithDomain() {
        return authenticateWithDomain;
    }

    public void setAuthenticateWithDomain(boolean authenticateWithDomain) {
        this.authenticateWithDomain = authenticateWithDomain;
    }

    /**
     * Check if translation has no modifiers.
     * @return true if no modifiers.
     */
    @JsonIgnore
    public boolean isEmpty() {
        return modifiers == null || modifiers.isEmpty();
    }

    boolean trimDomainIfPresent(Credentials credentials) {
        String username = credentials.getUsername();
        int index = username.lastIndexOf("@");
        if (index > -1) {
            /* Potential security issue here.
             * We should truncate username only when truncating is
             * safe to perform.
             */
            username = username.substring(0, index);
            credentials.setUsername(username);
            return true;
        }
        return false;
    }

    /**
     * Translate credentials.
     * @param credentials credentials.
     * @return translated credentials.
     */
    public Credentials translate(Credentials credentials) {
        if (credentials == null) {
            throw new IllegalArgumentException("Credentials can not be empty.");
        }

        if (!authenticateWithDomain) {
            trimDomainIfPresent(credentials);
        }

        Credentials result = Credentials.of(
                credentials.getUsername(), credentials.getPassword(),
                credentials.getIp(), credentials.getMac());

        if (isEmpty()) {
            return result;
        }

        if (modifiers != null) {
            for (CredentialsModifier modifier : modifiers) {
                result = modifier.modify(result);
            }
        }

        if (!authenticateWithDomain) {
            trimDomainIfPresent(result);
        }

        if (this.encoder != null) {
            result = this.encoder.encode(result, encoderAdditional);
        }

        return result;
    }

    @Override
    public String toString() {
        return "CredentialsTranslation{" +
                "id=" + id +
                ", modifiers=" + modifiers +
                ", encoder=" + encoder +
                ", encoderAdditional='" + encoderAdditional + '\'' +
                ", authenticateWithDomain=" + authenticateWithDomain +
                '}';
    }
}
