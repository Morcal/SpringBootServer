package cn.com.xinli.radius.type;

/**
  * <p>Project: xpws
 *
 * @author zhoupeng 2016/1/26.
 */
public class Framed {
    public enum FramedProtocol {
        PPP(1, "PPP"),
        SLIP(2, "SLIP"),
        ARAP(3, "ARAP"),
        GANDALF_SLML(4, "Gandalf-SLML"),
        XYLOGICS_IPX_SLIP(5, "Xylogics-IPX-SLIP"),
        X75_SYNCHRONOUS(6, "X.75-Synchronous"),
        GPRS_PDP_Context(7, "GPRS-PDP-Context");

        public static final String RADIUS_NAME = "Framed-Protocol";

        private final int value;
        private final String name;

        FramedProtocol(int value, String name) {
            this.value = value;
            this.name = name;
        }

        public int getValue() {
            return value;
        }

        public String getName() {
            return name;
        }
    }

    public enum FramedRouting {
        NONE(0, "None"),
        BROADCAST(1, "Broadcast"),
        LISTEN(2, "Listen"),
        BROADCAST_LISTEN(3, "Broadcast-Listen");

        public static final String RADIUS_NAME = "Framed-Routing";

        private final int value;
        private final String name;

        FramedRouting(int value, String name) {
            this.value = value;
            this.name = name;
        }

        public int getValue() {
            return value;
        }

        public String getName() {
            return name;
        }
    }

    public enum FramedCompression {
        NONE(0, "None"),
        VAN_JACOBSON_TCP_IP(1, "Van-Jacobson-TCP-IP"),
        IPX_HEADER_COMPRESSION(2, "IPX-Header-Compression"),
        STAC_LZS(3, "Stac-LZS");

        public static final String RADIUS_NAME = "Framed-Compression";

        private final int value;
        private final String name;

        FramedCompression(int value, String name) {
            this.value = value;
            this.name = name;
        }

        public int getValue() {
            return value;
        }

        public String getName() {
            return name;
        }
    }
}
