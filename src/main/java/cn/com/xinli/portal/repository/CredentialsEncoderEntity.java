package cn.com.xinli.portal.repository;

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
@Table(schema = "PWS", name="credentials_encoder")
public class CredentialsEncoderEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(nullable = false)
    private String algorithm;

    @Column
    private String value;

    public String getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "CredentialsEncoderEntity{" +
                "algorithm='" + algorithm + '\'' +
                ", id=" + id +
                ", value='" + value + '\'' +
                '}';
    }
}
