package cn.com.xinli.nio;

import java.io.Serializable;

/**
 * Portal protocol packet.
 *
 * <p>This interface doesn't have any methods.
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2015/12/24.
 */
public interface Packet extends Serializable {
    String EMPTY_PACKET = "Packet is empty.";
}
