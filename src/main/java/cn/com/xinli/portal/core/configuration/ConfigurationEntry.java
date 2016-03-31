package cn.com.xinli.portal.core.configuration;

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
public class ConfigurationEntry {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonProperty
    private long id;

    @Column(name = "key_text", unique = true, nullable = false)
    private String key;

    @Column(name = "value_type", nullable = false)
    private Configuration.ValueType valueType;

    @Column(name = "value_text", nullable = true)
    private String valueText;

    @Transient
    private Object value;

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

    public void setValueType(Configuration.ValueType valueType) {
        this.valueType = valueType;
    }

    public String getValueText() {
        return valueText;
    }

    /**
     * Read value into this entry.
     * @param value value.
     */
    public void updateValue(String value) {
        this.valueText = value;

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
    <T> T getValue() {
        return valueType.cast(value);
    }

    /**
     * Create entry from key and value type.
     * @param key entry key.
     * @param valueType entry value type.
     * @return entry.
     */
    public static ConfigurationEntry of(String key, Configuration.ValueType valueType, String value) {
        ConfigurationEntry entry = new ConfigurationEntry();
        entry.setKey(key);
        entry.setValueType(valueType);
        entry.updateValue(value);
        return entry;
    }

    @Override
    public String toString() {
        return "ConfigurationEntry{" +
                "id=" + id +
                ", key='" + key + '\'' +
                ", valueType=" + valueType +
                ", valueText='" + valueText + '\'' +
                ", value=" + value +
                '}';
    }
}
