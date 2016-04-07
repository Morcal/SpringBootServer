package cn.com.xinli.portal.core.runtime;

import cn.com.xinli.portal.core.nas.Nas;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Stream;

/**
 * System runtime.
 * @author zhoupeng, created on 2016/3/27.
 */
@Component
public class Runtime {
    /** Default date format. */
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");

    /** NAS statistics map. */
    private final Map<Long, NasStatistics> nasStatisticsMap =
            Collections.synchronizedMap(new HashMap<>());

    /** Session statistics. */
    private final SessionStatistics sessionStatistics = new SessionStatistics();

    /** System load statistics. */
    private final LoadStatistics loadStatistics = new LoadStatistics();

    /** Total session statistics. */
    private final TotalSessionStatistics totalSessionStatistics = new TotalSessionStatistics();

    public static String formatDateTime(Date date) {
        return dateFormat.format(date);
    }

    public Map<Long, NasStatistics> getNasStatisticsMap() {
        return Collections.unmodifiableMap(nasStatisticsMap);
    }

    public SessionStatistics getSessionStatistics() {
        return sessionStatistics;
    }

    public LoadStatistics getLoadStatistics() {
        return loadStatistics;
    }

    public TotalSessionStatistics getTotalSessionStatistics() {
        return totalSessionStatistics;
    }

    /**
     * Add a session record.
     * @param record session record.
     */
    public synchronized void addSessionRecord(SessionStatistics.SessionRecord record) {
        sessionStatistics.addRecord(record);
        totalSessionStatistics.addRecord(record);
    }

    /**
     * Add a nas record.
     * @param record nas record.
     */
    public synchronized void addNasRecord(NasStatistics.NasRecord record) {
        Objects.requireNonNull(record, "Nas record can not be null.");

        synchronized (nasStatisticsMap) {
            NasStatistics statistics = nasStatisticsMap.get(record.getNasId());
            if (statistics != null) {
                statistics.addRecord(record);
            }
        }
    }

    /**
     * Add a load record.
     * @param record load record.
     */
    public synchronized void addLoadRecord(LoadStatistics.LoadRecord record) {
        loadStatistics.addRecord(record);
    }

    /**
     * Initialize system runtime.
     */
    public void init(Stream<Nas> devices) {
        devices.forEach(n -> {
            synchronized (nasStatisticsMap) {
                NasStatistics nas = new NasStatistics(n.getId(), n.getName());
                nasStatisticsMap.put(n.getId(), nas);
            }
        });
    }
}
