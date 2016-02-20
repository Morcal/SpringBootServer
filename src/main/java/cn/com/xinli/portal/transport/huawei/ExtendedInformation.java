package cn.com.xinli.portal.transport.huawei;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Session extended information.
 *
 * <p>HUAWEI portal protocol requires that <code>request id</code>s in
 * <code>REQ_AUTH, ACK_AUTH, AFF_ACK_AUTH, REQ_LOGOUT, ACK_LOGOUT</code>
 * are the same as in <code>ACK_CHALLENGE</code>. To implement that,
 * server need save <code>request id</code> (issued by NAS/BRAS) in
 * <code>ACK_CHALLENGE</code> response and reference it in subsequent requests.
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2016/2/18.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ExtendedInformation {
    /** Request id issued by NAS/BRAS. */
    @JsonProperty("request_id")
    private int requestId;

    public int getRequestId() {
        return requestId;
    }

    public void setRequestId(int requestId) {
        this.requestId = requestId;
    }
}
