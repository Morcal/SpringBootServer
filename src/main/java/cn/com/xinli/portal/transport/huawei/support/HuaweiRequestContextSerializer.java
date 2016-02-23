package cn.com.xinli.portal.transport.huawei.support;

import cn.com.xinli.portal.core.session.SessionExtendedInformationSerializer;
import cn.com.xinli.portal.transport.huawei.RequestContext;
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
public class HuaweiRequestContextSerializer implements SessionExtendedInformationSerializer<RequestContext> {
    /** Logger. */
    private final Logger logger = LoggerFactory.getLogger(HuaweiRequestContextSerializer.class);

    /** JSON object mapper. */
    private ObjectMapper mapper;

    public HuaweiRequestContextSerializer() {
        mapper = new ObjectMapper();
    }

    @Override
    public Optional<String> serialize(RequestContext context) {
        Objects.requireNonNull(context);
        try {
            return Optional.of(mapper.writeValueAsString(context));
        } catch (JsonProcessingException e) {
            logger.warn("failed to serialize huawei context, {}", context);
            return Optional.empty();
        }
    }

    @Override
    public Optional<RequestContext> deserialize(String context) {
        Objects.requireNonNull(context);
        try {
            return Optional.of(mapper.readValue(context, RequestContext.class));
        } catch (IOException e) {
            logger.warn("failed to deserialize huawei context, {}", context);
            return Optional.empty();
        }
    }
}
