package cn.com.xinli.portal.protocol;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Project: xpws
 *
 * @author zhoupeng 2015/12/22.
 */
public class Message {
    private boolean success = false;
    private String content;
    private Packet packet;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public Packet getPacket() {
        return packet;
    }

    public static Message failure(String content) {
        Message message = new Message();
        message.setSuccess(false);
        message.setContent(content);
        message.packet = null;
        return message;
    }

    public static Message from(Packet packet) {
        Message message = new Message();
        message.setSuccess(true);
        // message.setContent(content);
        message.packet = packet;

        return message;
    }
}
