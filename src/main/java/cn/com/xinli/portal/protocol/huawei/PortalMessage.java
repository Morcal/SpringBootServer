package cn.com.xinli.portal.protocol.huawei;

import cn.com.xinli.portal.Message;

import java.util.NoSuchElementException;
import java.util.Optional;

/**
 * Huawei portal message.
 *
 * Project: xpws
 *
 * @author zhoupeng 2015/12/22.
 */
public class PortalMessage implements Message<HuaweiPacket> {
    private boolean success = false;
    private String text;
    private HuaweiPacket packet;

    @Override
    public String getText() {
        return text;
    }

    @Override
    public Optional<HuaweiPacket> getContent() {
        return Optional.ofNullable(packet);
    }

    @Override
    public boolean isSuccess() {
        return success;
    }

    private static String buildText(HuaweiPacket packet) {
        try {
            Optional<Enums.Type> type = Enums.Type.valueOf(packet.getType());

            int error = packet.getError();

            if (type.isPresent()) {
                switch (type.get()) {
                    case ACK_AUTH:
                        return Enums.AuthError.valueOf(error).get().getDescription();

                    case ACK_LOGOUT:
                        return Enums.LogoutError.valueOf(error).get().getDescription();

                    case ACK_CHALLENGE:
                        return Enums.ChallengeError.valueOf(error).get().getDescription();

                    default:
                        return "Unknown";
                }
            } else {
                return "Invalid packet type: " + packet.getType();
            }
        } catch (NoSuchElementException e) {
            return "Unknown packet error, packet type: " + packet.getType() +
                    ", error:" + packet.getError();
        }
    }

    public static PortalMessage from(HuaweiPacket packet) {
        PortalMessage message = new PortalMessage();
        message.success = packet.getError() == 0;
        message.packet = packet;

        Optional<HuaweiPacket.Attribute> text = packet.getAttributes().stream()
                .filter(attr -> attr.getType() == Enums.Attribute.TEXT_INFO.code())
                .findFirst();

        if (text.isPresent()) {
            message.text = new String(text.get().getValue());
        } else {
            message.text = buildText(packet);
        }

        return message;
    }

    @Override
    public String toString() {
        return "PortalMessage{" +
                "text='" + text +
                "', success=" + success +
                ", packet=" + packet +
                '}';
    }
}
