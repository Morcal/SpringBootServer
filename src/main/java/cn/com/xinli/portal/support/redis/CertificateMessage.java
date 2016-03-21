package cn.com.xinli.portal.support.redis;

import cn.com.xinli.portal.core.certificate.Certificate;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * <p>Project: xpws
 *
 * @author zhoupeng 2016/1/31.
 */
@JsonInclude
public class CertificateMessage {
    enum Type {
        ADDED,
        REMOVED,
        SUSPENDED
    }

    @JsonProperty
    private Type type;

    @JsonProperty
    private Certificate certificate;

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Certificate getCertificate() {
        return certificate;
    }

    public void setCertificate(Certificate certificate) {
        this.certificate = certificate;
    }
}
