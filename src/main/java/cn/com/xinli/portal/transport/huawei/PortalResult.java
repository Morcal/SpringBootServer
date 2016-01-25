package cn.com.xinli.portal.transport.huawei;

import cn.com.xinli.portal.transport.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

/**
 * Huawei portal message.
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
    private HuaweiPacket packet;

    @Override
    public String getText() {
        return text;
    }

    /**
     * Create portal message from a Huawei portal packet.
     * @param packet packet.
     * @return portal message.
     */
    public static PortalResult from(HuaweiPacket packet) {
        PortalResult result = new PortalResult();
        result.packet = packet;

        Optional<HuaweiPacket.Attribute> text = packet.getAttributes().stream()
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
