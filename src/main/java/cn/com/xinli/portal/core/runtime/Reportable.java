package cn.com.xinli.portal.core.runtime;

/**
 * Reportable.
 *
 * <p>Classes implement this interface can export a report.
 * @author zhoupeng, created on 2016/4/8.
 */
public interface Reportable {
    /**
     * Generate report.
     * @return report.
     */
    Report report();
}
