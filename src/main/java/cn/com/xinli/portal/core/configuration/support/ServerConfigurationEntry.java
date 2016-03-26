package cn.com.xinli.portal.core.configuration.support;

import cn.com.xinli.portal.core.activity.Activity;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;

/**
 * Server configuration entry.
 * @author zhoupeng, created on 2016/3/25.
 */
@Entity
@PersistenceUnit(unitName = "system")
@Table(schema = "PWS", name="server_configuration")
public class ServerConfigurationEntry {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonProperty
    private long id;

    @Column(name = "key_text", unique = true, nullable = false)
    private String key;

    @Column(name = "value_type", nullable = false)
    private ValueType valueType;

    @Column(name = "value_text", nullable = false)
    private String valueText;

    @Transient
    private Object value;


    enum ValueType {
        BOOLEAN(Boolean.class),
        INTEGER(Integer.class),
        STRING(String.class),
        SEVERITY(Activity.Severity.class);

        private final Class<?> cls;

        ValueType(Class<?> cls) {
            this.cls = cls;
        }
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public ValueType getValueType() {
        return valueType;
    }

    public void setValueType(ValueType valueType) {
        this.valueType = valueType;
    }

    public String getValueText() {
        return valueText;
    }

    public void setValueText(String valueText) {
        this.valueText = valueText;
    }

    /**
     * Read value into this entry.
     * @param value value.
     */
    void readValue(String value) {
        switch (valueType) {
            case BOOLEAN:
                this.value = Boolean.parseBoolean(value);
                break;

            case INTEGER:
                this.value = Integer.parseInt(value);
                break;

            case STRING:
                this.value = value;
                break;

            case SEVERITY:
                this.value = Activity.Severity.valueOf(value);
                break;
        }
    }

    /**
     * Get entry value.
     * @param <T> value type.
     * @return entry value.
     */
    @SuppressWarnings("unchecked")
    <T> T getValue() {
        return (T) valueType.cls.cast(value);
    }

    /**
     * Create entry from key and value type.
     * @param key entry key.
     * @param valueType entry value type.
     * @return entry.
     */
    static ServerConfigurationEntry of(String key, ValueType valueType) {
        ServerConfigurationEntry entry = new ServerConfigurationEntry();
        entry.key = key;
        entry.valueType = valueType;
        return entry;
    }
}
