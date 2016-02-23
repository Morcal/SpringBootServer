package cn.com.xinli.portal.transport.huawei;

import cn.com.xinli.portal.transport.Context;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
public class RequestContext implements Context {
    /** Request id issued by NAS/BRAS. */
    @JsonProperty("request_id")
    private int requestId;

    /** Text information. */
    @JsonIgnore
    private String textInfo;

    /** Empty context. */
    @JsonIgnore
    private static final RequestContext EMPTY = new RequestContext();

    /**
     * Get empty context.
     * @return empty context.
     */
    public static RequestContext empty() {
        return EMPTY;
    }

    @JsonIgnore
    public boolean isEmpty() {
        return this == EMPTY;
    }

    public int getRequestId() {
        return requestId;
    }

    public void setRequestId(int requestId) {
        this.requestId = requestId;
    }

    @JsonIgnore
    public String getTextInfo() {
        return textInfo;
    }

    public void setTextInfo(String textInfo) {
        this.textInfo = textInfo;
    }

    @Override
    public String toString() {
        if (isEmpty()) {
            return "EMPTY REQUEST CONTEXT";
        }

        return "RequestContext{" +
                "requestId=" + requestId +
                ", textInfo='" + textInfo + '\'' +
                '}';
    }
}
