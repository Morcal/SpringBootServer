package cn.com.xinli.portal.core.credentials;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import javax.persistence.*;

/**
 * Credentials encoder.
 *
 * <p>Classes implement this interface should not modify original credentials,
 * instead of modifying, implement classes should return a new credentials
 * which was encoded original credentials to.
 *
 * <p>To make jackson-json be able to deserialize to an abstract class (like this class),
 * this class was annotated with {@link JsonTypeInfo} to add additional subclass
 * information in JSON, and with {@link JsonSubTypes} to support mapper serializer.
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
@JsonInclude
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "encoder_type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = CredentialsEncoders.NoOpEncoder.class, name = "NO-OP"),
        @JsonSubTypes.Type(value = CredentialsEncoders.Md5Encoder.class, name = "MD5"),
        @JsonSubTypes.Type(value = CredentialsEncoders.Sha1HexEncoder.class, name = "SHA1-HEX"),
        @JsonSubTypes.Type(value = CredentialsEncoders.Sha1Base64Encoder.class, name = "SHA1-BASE64"),
})
public abstract class CredentialsEncoder {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonProperty
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
                "id=" + id +
                '}';
    }
}
