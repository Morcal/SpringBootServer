package cn.com.xinli.portal.util;

/**
 * Project: portal
 *
 * @author zhoupeng 2015/12/7.
 */
public abstract class AbstractTimeLimitedToken implements Token, TimeLimited {

    private final long createTime;
    private final long expire;
    private final String value;

    public AbstractTimeLimitedToken(String value, long expire) {
        if (expire <= 0) {
            throw new IllegalArgumentException("expire must be positive.");
        }
        createTime = System.currentTimeMillis();
        this.expire = expire * 1000L;
        this.value = value;
    }

    @Override
    public final boolean expired() {
        long now = System.currentTimeMillis();
        return now >= createTime + expire;
    }

    protected String getValue() {
        return value;
    }

    @Override
    public String text() {
        return getValue();
    }

    @Override
    public boolean equals(Object obj) {
        return obj != null && getClass().isAssignableFrom(obj.getClass())
                && getClass().cast(obj).getValue().equals(value);
    }

    @Override
    public int hashCode() {
        return getValue().hashCode();
    }
}
