package cn.com.xinli.radius;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * RADIUS packet.
 *
 * <p>This class represents a Radius packet. Subclasses provide convenience methods
 * for special packet types.
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2016/1/27.
 */
public class RadiusPacket {

    /** Type of this Radius packet. */
    private int packetType = 0;

    /** Identifier of this packet. */
    private int packetIdentifier = 0;

    /** Attributes for this packet. */
    private List<RadiusAttribute> attributes = new ArrayList<>();

    /** Authenticator for this Radius packet. */
    private byte[] authenticator = null;

    /** Next packet identifier. */
    private static AtomicInteger nextPacketId = new AtomicInteger(0);

}
