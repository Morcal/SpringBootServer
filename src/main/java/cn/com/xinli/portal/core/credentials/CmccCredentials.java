package cn.com.xinli.portal.core.credentials;

import com.fasterxml.jackson.annotation.JsonInclude;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * CMCC credentials.
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2016/2/2.
 */
@Entity
@DiscriminatorValue("CMCC")
@JsonInclude
public class CmccCredentials extends Credentials {
    @Override
    protected CredentialsType getCredentialsType() {
        return CredentialsType.CMCC;
    }
}
