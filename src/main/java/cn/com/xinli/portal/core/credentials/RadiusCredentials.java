package cn.com.xinli.portal.core.credentials;

import com.fasterxml.jackson.annotation.JsonInclude;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * RADIUS Credentials.
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2016/2/2.
 */
@Entity
@DiscriminatorValue("RADIUS")
@JsonInclude
public class RadiusCredentials extends Credentials {
    @Override
    protected CredentialsType getCredentialsType() {
        return CredentialsType.RADIUS;
    }
}
