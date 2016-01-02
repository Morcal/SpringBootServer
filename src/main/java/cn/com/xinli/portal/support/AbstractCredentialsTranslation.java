package cn.com.xinli.portal.support;

import cn.com.xinli.portal.Credentials;
import cn.com.xinli.portal.util.CredentialsEncoder;
import cn.com.xinli.portal.util.CredentialsModifier;
import cn.com.xinli.portal.CredentialsTranslation;

import java.util.Collection;

/**
 * Prefix-Postfix Credentials Modifier.
 *
 * This class's members are defined as "final" so that it
 * can be used by the "Flyweight" pattern.
 *
 * Project: xpws
 *
 * @author zhoupeng 2015/12/30.
 */
public class AbstractCredentialsTranslation implements CredentialsTranslation {
    /** Modifiers. */
    private final Collection<CredentialsModifier> modifiers;

    /** Encoder. */
    private final CredentialsEncoder encoder;

    /** Sole constructor. */
    public AbstractCredentialsTranslation(CredentialsEncoder encoder, Collection<CredentialsModifier> modifiers) {
        this.encoder = encoder;
        this.modifiers = modifiers;
    }

    private boolean isEmpty() {
        return modifiers.isEmpty();
    }

    @Override
    public Credentials translate(Credentials credentials) {
        if (credentials == null) {
            throw new IllegalArgumentException("Credentials can not be empty.");
        }

        Credentials result = new Credentials(credentials.getUsername(), credentials.getPassword(),
                credentials.getIp(), credentials.getMac());
        if (isEmpty()) {
            return result;
        }

        if (modifiers != null) {
            for (CredentialsModifier modifier : modifiers) {
                result = modifier.modify(result);
            }
        }

        if (this.encoder != null) {
            result = this.encoder.encode(result);
        }

        return result;
    }

}
