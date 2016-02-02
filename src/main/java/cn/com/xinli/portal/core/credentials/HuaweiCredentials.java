package cn.com.xinli.portal.core.credentials;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * User credentials for HUAWEI portal protocol based services.
 *
 * <p>HUAWEI portal protocol requires that <code>request id</code>s in
 * <code>REQ_AUTH, ACK_AUTH, AFF_ACK_AUTH, REQ_LOGOUT, ACK_LOGOUT</code>
 * are the same as in <code>ACK_CHALLENGE</code>. To implement that,
 * server need save <code>request id</code> (issued by NAS/BRAS) in
 * <code>ACK_CHALLENGE</code> response and reference it in subsequent requests.
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2016/2/2.
 */
@Entity
@DiscriminatorValue("HUAWEI")
@JsonInclude
public class HuaweiCredentials extends Credentials {
    /** Request id issued by NAS/BRAS. */
    @JsonProperty("request_id")
    @Column(name = "request_id")
    private int requestId;

    @Override
    protected CredentialsType getCredentialsType() {
        return CredentialsType.HUAWEI;
    }

    public int getRequestId() {
        return requestId;
    }

    public void setRequestId(int requestId) {
        this.requestId = requestId;
    }

    public static HuaweiCredentials of(String username, String password, String ip, String mac, int requestId) {
        HuaweiCredentials credentials = new HuaweiCredentials();
        credentials.setUsername(username);
        credentials.setPassword(password);
        credentials.setIp(ip);
        credentials.setMac(mac);
        credentials.setRequestId(requestId);
        return credentials;
    }

    @Override
    public String toString() {
        return super.toString() + "HuaweiCredentials{" +
                "requestId=" + requestId +
                '}';
    }
}
