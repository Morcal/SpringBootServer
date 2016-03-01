package cn.com.xinli.portal.transport.huawei;

import org.apache.commons.lang3.RandomStringUtils;

import java.util.concurrent.atomic.AtomicLong;

/**
  * <p>Project: xpws
 *
 * @author zhoupeng 2016/1/30.
 */
public class Challenge {
    final int reqId;
    final long id;
    final long createTime;
    final String value;

    private final static AtomicLong challengeId = new AtomicLong(0);

    private static final Challenge EMPTY = new Challenge(-1);

    public static Challenge empty() {
        return EMPTY;
    }

    public Challenge(int reqId) {
        this.reqId = reqId;
        id = challengeId.incrementAndGet();
        createTime = System.currentTimeMillis();
        value = RandomStringUtils.randomAlphanumeric(16);
    }

    public boolean isEmpty() {
        return equals(EMPTY);
    }

    public int getReqId() {
        return reqId;
    }

    public long getId() {
        return id;
    }

    public long getCreateTime() {
        return createTime;
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Challenge challenge = (Challenge) o;
        return id == challenge.id && reqId == challenge.reqId &&
                createTime == challenge.createTime && value.equals(challenge.value);
    }

    @Override
    public int hashCode() {
        int result = reqId;
        result = 31 * result + (int) (id ^ (id >>> 32));
        result = 31 * result + (int) (createTime ^ (createTime >>> 32));
        result = 31 * result + value.hashCode();
        return result;
    }
}
