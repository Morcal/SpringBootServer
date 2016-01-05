package cn.com.xinli.portal.persist;

import cn.com.xinli.portal.protocol.CredentialsModifier;

import javax.persistence.*;

/**
 * Credentials modifier entity.
 *
 * Project: xpws
 *
 * @author zhoupeng 2016/1/1.
 */
@Entity
@PersistenceUnit(unitName = "system")
@Table(schema = "PWS", name="credentials_modifier")
public class CredentialsModifierEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(nullable = false)
    private CredentialsModifier.Target target;

    @Column(nullable = false)
    private CredentialsModifier.Position position;

    @Column(nullable = false)
    private String value;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public CredentialsModifier.Position getPosition() {
        return position;
    }

    public void setPosition(CredentialsModifier.Position position) {
        this.position = position;
    }

    public CredentialsModifier.Target getTarget() {
        return target;
    }

    public void setTarget(CredentialsModifier.Target target) {
        this.target = target;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "CredentialsModifierEntity{" +
                "id=" + id +
                ", target=" + target +
                ", position=" + position +
                ", value='" + value + '\'' +
                '}';
    }
}
