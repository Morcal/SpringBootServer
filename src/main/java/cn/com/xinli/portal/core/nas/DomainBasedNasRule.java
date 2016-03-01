package cn.com.xinli.portal.core.nas;

import cn.com.xinli.portal.core.credentials.Credentials;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;
import java.util.stream.Stream;

/**
 * Domain based NAS rule.
 *
 * <p>This class provides a domain-name based rule for credentials-NAS matching.
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2016/1/23.
 */
@Entity
@DiscriminatorValue("DOMAIN")
public class DomainBasedNasRule extends NasRule {
    /** Supported domains, separated by comma. */
    @Column(name = "supported_domains")
    private String supportedDomains;

    @Transient
    private String[] domains;

    public void setSupportedDomains(String supportedDomains) {
        this.supportedDomains = supportedDomains;
    }

    @Override
    protected boolean matchInternal(Credentials credentials) {
        //FIXME take another more efficient approaches.
        synchronized (this) {
            if (domains == null) {
                domains = supportedDomains.split(",");
            }
        }

        String username = credentials.getUsername();

        if (!StringUtils.isEmpty(username) && username.contains("@")) {
            String domain = username.substring(username.indexOf("@"));
            return Stream.of(domains)
                    .filter(domain::equals)
                    .findAny()
                    .isPresent();
        }
        return false;
    }

}
