package cn.com.xinli.portal.core.credentials;

import javax.persistence.*;

/**
 * Credentials encoder.
 *
 * <p>Classes implement this interface should not modify original credentials,
 * instead of modifying, implement classes should return a new credentials
 * which was encoded original credentials to.
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2015/12/30.
 */
@Entity
@PersistenceUnit(unitName = "system")
@Table(schema = "PWS", name="credentials_encoder")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "encoder_type", discriminatorType = DiscriminatorType.STRING)
public abstract class CredentialsEncoder {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    /**
     * Modify credentials and return a new encoded one.
     * @param credentials original credentials.
     * @param additional additional information for encoding.
     * @return modified new credentials.
     */
    public abstract Credentials encode(Credentials credentials, String additional);

    @Override
    public String toString() {
        return "CredentialsEncoder{" +
                ", id=" + id +
                '}';
    }
}
