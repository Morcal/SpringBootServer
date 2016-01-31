package cn.com.xinli.portal.core.credentials;

import org.apache.commons.lang3.StringUtils;

import javax.persistence.*;

/**
 * Credentials modifier.
 *
 * Project: xpws
 *
 * @author zhoupeng 2015/12/30.
 */
@Entity
@PersistenceUnit(unitName = "system")
@Table(schema = "PWS", name="credentials_modifier")
public class CredentialsModifier {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    protected long id;

    @Column(nullable = false)
    protected CredentialsModifier.Target target;

    @Column(nullable = false)
    protected CredentialsModifier.Position position;

    @Column(nullable = false)
    protected String value;

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
        return "CredentialsModifier{" +
                "id=" + id +
                ", target=" + target +
                ", position=" + position +
                ", value='" + value + '\'' +
                '}';
    }

    /** Credentials modify target. */
    public enum Target {
        /** Modify username in credentials. */
        USERNAME,
        /** Modify password in credentials. */
        PASSWORD
    }

    /** Modify target position. */
    public enum Position {
        /** Modify target's head. */
        HEAD,
        /** Modify target's tail. */
        TAIL
    }

    /**
     * Check if this modifier is empty.
     * @return true if modifier has no value, target or position.
     */
    boolean isEmpty() {
        return StringUtils.isEmpty(value) || target == null || position == null;
    }

    /**
     * Modify internal values.
     * @param source source value.
     * @return modified value.
     */
    private String modify(String source) {
        StringBuilder builder = new StringBuilder();
        switch (position) {
            case HEAD:
                if (!StringUtils.isEmpty(source)) {
                    if (!StringUtils.isEmpty(value)) {
                        builder.append(value);
                    }
                    builder.append(StringUtils.defaultString(source, ""));
                }
                break;

            case TAIL:
                builder.append(StringUtils.defaultString(source, ""));
                if (!StringUtils.isEmpty(value)) {
                    builder.append(value);
                }
                break;

            default:
                break;
        }
        return builder.toString();
    }

    /**
     * Modify credentials and return a new one.
     * @param credentials original credentials.
     * @return modified new credentials.
     */
    public Credentials modify(Credentials credentials) {
        if (isEmpty()) {
            return credentials;
        }

        String username = credentials.getUsername(),
                password = credentials.getPassword();
        switch (target) {
            case USERNAME:
                username = modify(username);
                break;

            case PASSWORD:
                password = modify(password);
                break;
            default:
                break;
        }

        return Credentials.of(username, password, credentials.getIp(), credentials.getMac());
    }
}
