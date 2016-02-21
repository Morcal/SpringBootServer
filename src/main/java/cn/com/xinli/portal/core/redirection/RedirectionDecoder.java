package cn.com.xinli.portal.core.redirection;

import java.util.Map;

/**
 * Project: xpws
 *
 * @author zhoupeng 2016/2/21.
 */
public interface RedirectionDecoder {
    Map<String, String> decode(Redirection redirection);
}
