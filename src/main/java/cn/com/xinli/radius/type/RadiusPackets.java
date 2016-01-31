package cn.com.xinli.radius.type;

import cn.com.xinli.radius.RadiusPacket;

/**
 * Project: xpws
 *
 * @author zhoupeng 2016/1/27.
 */
public class RadiusPackets {

    public static RadiusPacket newAccessRequest() {
        RadiusPacket packet = new RadiusPacket();
        return packet;
    }
}
