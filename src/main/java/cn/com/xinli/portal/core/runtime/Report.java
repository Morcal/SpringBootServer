package cn.com.xinli.portal.core.runtime;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.*;

/**
 * Runtime report.
 * @author zhoupeng, created on 2016/3/29.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Report {
    /** Metadata. */
    @JsonProperty
    private Metadata metadata;

    @JsonProperty
    private final List<String> labels = new ArrayList<>();

    /** Data. */
    @JsonProperty("datasets")
    private final List<DataSet> dataSets = new ArrayList<>();

    public Metadata getMetadata() {
        return metadata;
    }

    public void setMetadata(Metadata metadata) {
        this.metadata = metadata;
    }

    public List<String> getLabels() {
        return labels;
    }

    public void setLabels(Collection<String> labels) {
        this.labels.addAll(labels);
    }

    public void addDataSet(DataSet dataSet) {
        Objects.requireNonNull(dataSet);
        dataSets.add(dataSet);
    }

    public List<DataSet> getDataSets() {
        return dataSets;
    }

    /**
     * Report metadata.
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public class Metadata {
        /** Data group. */
        @JsonProperty
        private int group;

        public int getGroup() {
            return group;
        }

        public void setGroup(int group) {
            this.group = group;
        }
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class DataSet {
        @JsonProperty
        private String name;

        @JsonProperty("data")
        private final List<Long> values = new ArrayList<>();

        public List<Long> getValues() {
            return values;
        }

        public void addValue(long value) {
            values.add(value);
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
