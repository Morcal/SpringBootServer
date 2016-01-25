package cn.com.xinli.portal.core;

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
public class CredentialsTranslation {
    /** Internal translation id. */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    /** Associated foreign modifiers. */
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "translation_modifier", schema = "PWS",
            joinColumns = @JoinColumn(name = "trans_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "mod_id", referencedColumnName = "id"))
    private List<CredentialsModifier> modifiers;

    /** Associated credentials encoder. */
    @ManyToOne
    @JoinColumn(name = "encoder_id", referencedColumnName = "id")
    private CredentialsEncoder encoder;

    /** Optional encoder additional value. */
    @Column(name = "encoder_value")
    private String encoderAdditional;

    /** NAS truncate domain when authenticate or not. */
    @Column(name ="authenticate_with_domain", nullable = false)
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

    /**
     * Check if translation has no modifiers.
     * @return true if no modifiers.
     */
    public boolean isEmpty() {
        return modifiers.isEmpty();
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

        String username = credentials.getUsername();
        if (!authenticateWithDomain) {
            int index = username.lastIndexOf("@");
            if (index > -1) {
                /* Potential security issue here.
                 * We should truncate username only when truncating is
                 * safe to perform.
                 */
                username = username.substring(0, index - 1);
            }
        }

        Credentials result = new Credentials(username, credentials.getPassword(),
                credentials.getIp(), credentials.getMac());


        if (isEmpty()) {
            return result;
        }

        if (modifiers != null) {
            for (CredentialsModifier modifier : modifiers) {
                result = modifier.modify(result);
            }
        }

        if (this.encoder != null) {
            result = this.encoder.encode(result, encoderAdditional);
        }

        return result;
    }
}
