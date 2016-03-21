package cn.com.xinli.radius.type;

/**
 * <p>Project: xpws
 *
 * @author zhoupeng 2016/1/27.
 */
public class ExtVendor {
    public enum ExtVendorAttribute {
        WISPR(14122, "WISPr");

        private final int value;
        private final String name;

        ExtVendorAttribute(int value, String name) {
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

    public enum ExtVendorSubAttribute {
        WISPR_LOCATION_ID(1, "WISPr-Location-ID", "string"),
        WISPR_LOCATION_NAME(2, "WISPr-Location-Name", "string"),
        WISPR_LOGOFF_URL(3, "WISPr-Logoff-URL", "string"),
        WISPR_REDIRECTION_URL(4, "WISPr-Redirection-URL", "string"),
        WISPR_BANDWIDTH_MIN_UP(5, "WISPr-Bandwidth-Min-Up", "integer"),
        WISPR_BANDWIDTH_MIN_DOWN(6, "WISPr-Bandwidth-Min-Down", "integer"),
        WISPR_BANDWIDTH_MAX_UP(7, "WISPr-Bandwidth-Max-Up", "integer"),
        WISPR_BANDWIDTH_MAX_DOWN(8, "WISPr-Bandwidth-Max-Down", "integer"),
        WISPR_SESSION_TERMINATE_TIME(9, "WISPr-Session-Terminate-Time", "string"),
        WISPR_SESSION_TERMINATE_END_OF_DAY(10, "WISPr-Session-Terminate-End-Of-Day", "integer"),
        WISPR_BILLING_CLASS_OF_SERVICE(11, "WISPr-Billing-Class-Of-Service", "string");

        private final int value;

        private final String name;

        private final String type;

        ExtVendorSubAttribute(int value, String name, String type) {
            this.value = value;
            this.name = name;
            this.type = type;
        }

        public int getValue() {
            return value;
        }

        public String getName() {
            return name;
        }

        public String getType() {
            return type;
        }
    }
}
