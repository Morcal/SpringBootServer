package cn.com.xinli.portal.transport.huawei.support;

import cn.com.xinli.portal.transport.Result;
import cn.com.xinli.portal.transport.huawei.AttributeType;
import cn.com.xinli.portal.transport.huawei.Packet;
import cn.com.xinli.portal.transport.huawei.Packets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

/**
 * HUAWEI portal message.
 *
 * Project: xpws
 *
 * @author zhoupeng 2015/12/22.
 */
final class PortalResult implements Result {
    /** Logger. */
    private final Logger logger = LoggerFactory.getLogger(PortalResult.class);

    /** Result text content. */
    private String text;

    /** Packet inside this message. */
    private Packet packet;

    @Override
    public String getText() {
        return text;
    }

    /**
     * Create portal message from a HUAWEI portal packet.
     * @param packet packet.
     * @return portal message.
     */
    public static PortalResult from(Packet packet) {
        PortalResult result = new PortalResult();
        result.packet = packet;

        Optional<Packet.Attribute> text = packet.getAttributes().stream()
                .filter(attr -> attr.getType() == AttributeType.TEXT_INFO.code())
                .findFirst();

        if (text.isPresent()) {
            result.text = new String(text.get().getValue());
        } else {
            result.text = Packets.buildText(packet);
        }

        if (result.logger.isTraceEnabled()) {
            result.logger.trace("Portal result: {}", result);
        }

        return result;
    }

    @Override
    public String toString() {
        return "PortalResult{" +
                "text='" + text + '\'' +
                ", packet=" + packet +
                '}';
    }
}
