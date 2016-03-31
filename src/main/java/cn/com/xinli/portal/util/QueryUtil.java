package cn.com.xinli.portal.util;

import cn.com.xinli.portal.core.PortalError;
import cn.com.xinli.portal.core.RemoteException;
import org.apache.commons.lang3.StringUtils;

/**
 * Query helper utility.
 * @author zhoupeng, created on 2016/3/31.
 */
public class QueryUtil {
    private static final String[] FORBIDDEN_QUERY_WORDS = {
            "insert", "update", ";", "select", "delete", "truncate", "drop", "use", "database"
    };

    /**
     * Query security check.
     * @param query query.
     * @throws RemoteException
     */
    public static void checkQuery(String query) throws RemoteException {
        if (!StringUtils.isEmpty(query)) {
            for (String word : FORBIDDEN_QUERY_WORDS) {
                if (StringUtils.contains(query.toLowerCase(), word)) {
                    throw new RemoteException(PortalError.INVALID_REQUEST);
                }
            }
        }
    }
}
