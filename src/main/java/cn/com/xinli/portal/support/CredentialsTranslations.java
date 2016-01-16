package cn.com.xinli.portal.support;

import cn.com.xinli.portal.repository.CredentialsEncoderEntity;
import cn.com.xinli.portal.repository.CredentialsTranslationEntity;
import cn.com.xinli.portal.protocol.CredentialsEncoder;
import cn.com.xinli.portal.protocol.CredentialsModifier;
import cn.com.xinli.portal.protocol.CredentialsTranslation;
import cn.com.xinli.portal.protocol.support.AbstractCredentialsTranslation;
import cn.com.xinli.portal.protocol.support.CredentialsEncoders;
import cn.com.xinli.portal.protocol.support.PrefixPostfixCredentialsModifier;
import cn.com.xinli.portal.repository.CredentialsModifierEntity;
import cn.com.xinli.portal.repository.NasEntity;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Credentials translations.
 * <p>
 * Project: xpws
 *
 * @author zhoupeng 2015/12/30.
 */
public abstract class CredentialsTranslations {
    /**
     * Flyweight translations.
     */
    private static final Map<Long, CredentialsTranslation> translations =
            Collections.synchronizedMap(new HashMap<>());

    private static CredentialsModifier createCredentialsModifier(CredentialsModifierEntity entity) {
        return new PrefixPostfixCredentialsModifier(
                entity.getTarget(), entity.getPosition(), entity.getValue());
    }

    public static CredentialsTranslation getTranslation(NasEntity entity) {
        long id = entity.getId();
        CredentialsTranslationEntity translationEntity = entity.getTranslation();
        synchronized (translations) {
            if (!translations.containsKey(id)) {
                List<CredentialsModifier> modifiers
                        = translationEntity.getModifiers() == null ? Collections.emptyList() :
                        translationEntity.getModifiers().stream()
                                .map(CredentialsTranslations::createCredentialsModifier)
                                .collect(Collectors.toList());

                CredentialsEncoderEntity cee = translationEntity.getEncoder();
                CredentialsEncoder encoder = cee == null ? null :
                        CredentialsEncoders.getEncoder(cee.getAlgorithm(), cee.getValue());
                CredentialsTranslation translation = new AbstractCredentialsTranslation(encoder, modifiers);
                translations.put(id, translation);
            }
            return translations.get(id);
        }
    }
}
