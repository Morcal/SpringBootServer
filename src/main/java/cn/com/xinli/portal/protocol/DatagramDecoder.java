package cn.com.xinli.portal.protocol;

import java.net.DatagramPacket;

/**
 * Project: portal
 *
 * @author zhoupeng 2015/12/2.
 */
public interface DatagramDecoder {
    Packet decode(DatagramPacket in);
}
