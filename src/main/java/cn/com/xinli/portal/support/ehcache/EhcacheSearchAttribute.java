package cn.com.xinli.portal.support.ehcache;

import net.sf.ehcache.config.SearchAttribute;

/**
 * EhCache search attribute.
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2016/2/3.
 */
public class EhcacheSearchAttribute {
    /** Attribute name. */
    private final String name;

    /** Search attribute type. */
    private final Class<?> type;

    /** Value evaluation expression. */
    private final String expression;

    public EhcacheSearchAttribute(String name, Class<?> type, String expression) {
        this.name = name;
        this.type = type;
        this.expression = expression;
    }

    public String getName() {
        return name;
    }

    public Class<?> getType() {
        return type;
    }

    public String getExpression() {
        return expression;
    }

    public SearchAttribute toEhcacheAttribute() {
        return new SearchAttribute().name(name).type(type).expression(expression);
    }
}
