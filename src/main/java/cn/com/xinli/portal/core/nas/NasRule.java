package cn.com.xinli.portal.core.nas;

import cn.com.xinli.portal.core.Matcher;
import cn.com.xinli.portal.core.credentials.Credentials;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import javax.persistence.*;
import java.util.Objects;

/**
 * NAS rule.
 *
 * <p>Classes implement this interface for specific NAS configuration
 * so that it can provide a fail-safe rule when server can not find
 * which NAS remote client came from by other means.
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2016/1/23.
 */
@Entity
@PersistenceUnit(unitName = "system")
@Table(schema = "PWS", name = "nas_rule")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "rule_type", discriminatorType = DiscriminatorType.STRING)
@JsonInclude
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "rule_type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = IPv4RangeBasedNasRule.class, name = "IPv4-RANGE"),
        @JsonSubTypes.Type(value = DomainBasedNasRule.class, name = "DOMAIN"),
})
public abstract class NasRule implements Matcher<Credentials> {
    public static final String EMPTY_RULE = "Nas rule is empty.";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nas_id", referencedColumnName = "id")
    private Nas nas;

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

    /**
     * Perform matching on credentials.
     * @param credentials credentials to match.
     * @return true if matches.
     */
    protected abstract boolean matchInternal(Credentials credentials);

    @Override
    public final boolean matches(Credentials credentials) {
        Objects.requireNonNull(credentials, Credentials.EMPTY_CREDENTIALS);
        return matchInternal(credentials);
    }
}
