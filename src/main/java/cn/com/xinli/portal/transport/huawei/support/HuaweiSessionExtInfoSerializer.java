package cn.com.xinli.portal.transport.huawei.support;

import cn.com.xinli.portal.core.session.SessionExtendedInformationSerializer;
import cn.com.xinli.portal.transport.huawei.ExtendedInformation;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

/**
 * HUAWEI session extended information serializer.
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2016/2/18.
 */
public class HuaweiSessionExtInfoSerializer implements SessionExtendedInformationSerializer<ExtendedInformation> {
    /** Logger. */
    private final Logger logger = LoggerFactory.getLogger(HuaweiSessionExtInfoSerializer.class);

    /** JSON object mapper. */
    private ObjectMapper mapper;

    public HuaweiSessionExtInfoSerializer() {
        mapper = new ObjectMapper();
    }

    @Override
    public Optional<String> serialize(ExtendedInformation information) {
        Objects.requireNonNull(information);
        try {
            return Optional.of(mapper.writeValueAsString(information));
        } catch (JsonProcessingException e) {
            logger.warn("failed to serialize huawei session extended information, {}", information);
            return Optional.empty();
        }
    }

    @Override
    public Optional<ExtendedInformation> deserialize(String value) {
        Objects.requireNonNull(value);
        try {
            return Optional.of(mapper.readValue(value, ExtendedInformation.class));
        } catch (IOException e) {
            logger.warn("failed to deserialize huawei session extended information, {}", value);
            return Optional.empty();
        }
    }
}
