package cn.com.xinli.portal.core.runtime;

import java.util.List;

/**
 * Reporter.
 *
 * @author zhoupeng, created on 2016/3/29.
 */
public interface Reporter {
    /**
     * Generate a report.
     * @param names report data value names.
     * @param aggregated aggregated records.
     * @param <T> record type.
     * @return report.
     */
    static <T extends Record> Report report(String[] names, List<AggregateRecord<T>> aggregated) {
        Report report = new Report();

        /* Create metadata. */
        Report.Metadata metadata = report.new Metadata();
        metadata.setGroup(names.length);
        report.setMetadata(metadata);

        /* Create labels. */
        report.setLabels(ReportCollector.toDates(aggregated));

        /* Fill data sets. */
        for (String name : names) {
            report.addDataSet(ReportCollector.toDataSet(name, aggregated));
        }

        return report;
    }
}
