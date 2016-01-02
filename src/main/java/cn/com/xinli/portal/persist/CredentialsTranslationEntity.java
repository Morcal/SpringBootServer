package cn.com.xinli.portal.persist;

import javax.persistence.*;
import java.util.List;

/**
 * Credentials modifier entity.
 *
 * Project: xpws
 *
 * @author zhoupeng 2016/1/1.
 */
@Entity
@PersistenceUnit(unitName = "system")
@Table(schema = "PWS", name="credentials_translation")
public class CredentialsTranslationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "translation_modifier", schema = "PWS",
            joinColumns = @JoinColumn(name = "trans_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "mod_id", referencedColumnName = "id"))
    private List<CredentialsModifierEntity> modifiers;

    @ManyToOne
    @JoinColumn(name = "encoder_id", referencedColumnName = "id")
    private CredentialsEncoderEntity encoder;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public List<CredentialsModifierEntity> getModifiers() {
        return modifiers;
    }

    public void setModifiers(List<CredentialsModifierEntity> modifiers) {
        this.modifiers = modifiers;
    }

    public CredentialsEncoderEntity getEncoder() {
        return encoder;
    }

    public void setEncoder(CredentialsEncoderEntity encoder) {
        this.encoder = encoder;
    }
}
