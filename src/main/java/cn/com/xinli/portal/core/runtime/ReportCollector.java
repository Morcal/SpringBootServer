package cn.com.xinli.portal.core.runtime;

import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Report collector.
 * @author zhoupeng, created on 2016/3/29.
 */
public interface ReportCollector {
    /**
     * Get records dates.
     * @param records records.
     * @param <T> record type.
     * @return list of record dates.
     */
    static <T extends Record> List<String> toDates(List<AggregateRecord<T>> records) {
        Objects.requireNonNull(records);
        return records.stream()
                .map(r -> Runtime.formatDateTime(r.getRecordedDate()))
                .collect(Collectors.toList());
    }

    /**
     * Collect records's values.
     * @param name record value name.
     * @param records records.
     * @param <T> record type.
     * @return list of values.
     */
    static <T extends Record> Report.DataSet toDataSet(String name, List<AggregateRecord<T>> records) {
        Objects.requireNonNull(name);
        Objects.requireNonNull(records);

        if (StringUtils.isEmpty(name)) {
            throw new IllegalArgumentException("label name can not be blank.");
        }

        Report.DataSet dataSet = new Report.DataSet();
        dataSet.setName(name);

        records.forEach(r -> dataSet.addValue(r.getValue(name)));

        return dataSet;
    }
}
