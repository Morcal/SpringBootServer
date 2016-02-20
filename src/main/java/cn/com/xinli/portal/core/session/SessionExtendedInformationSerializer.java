package cn.com.xinli.portal.core.session;

import java.util.Optional;

/**
 * Session Extended Information Serializer.
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2016/2/18.
 */
public interface SessionExtendedInformationSerializer<T> {
    /**
     * Serialize extended information to a string.
     * @param object extended information object.
     * @return serialized string.
     */
    Optional<String> serialize(T object);

    /**
     * Deserialize string to extended information.
     * @param value serialized string.
     * @return extended information.
     */
    Optional<T> deserialize(String value);
}
