package cn.com.xinli.portal.support.persist;

import cn.com.xinli.portal.core.credentials.CredentialsEncoder;
import cn.com.xinli.portal.core.credentials.CredentialsTranslation;
import cn.com.xinli.portal.core.nas.Nas;
import cn.com.xinli.portal.core.nas.NasRule;
import cn.com.xinli.portal.support.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.stream.Stream;

/**
 * NAS/BRAS device persistence.
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2016/1/31.
 */
@Component
public class NasPersistence {
    @Qualifier("nasRuleRepository")
    @Autowired
    private NasRuleRepository nasRuleRepository;

    @Qualifier("nasRepository")
    @Autowired
    private NasRepository nasRepository;

    @Qualifier("credentialsEncoderRepository")
    @Autowired
    private CredentialsEncoderRepository credentialsEncoderRepository;

    @Qualifier("credentialsModifierRepository")
    @Autowired

    private CredentialsModifierRepository credentialsModifierRepository;

    @Qualifier("credentialsTranslationRepository")
    @Autowired
    private CredentialsTranslationRepository credentialsTranslationRepository;

    public Iterable<NasRule> rules() {
        return nasRuleRepository.findAll();
    }

    public Iterable<Nas> devices() {
        return nasRepository.findAll();
    }

    public NasRule save(NasRule rule) {
        Objects.requireNonNull(rule, NasRule.EMPTY_RULE);
        return nasRuleRepository.save(rule);
    }

    public Stream<Nas> search(String value) {
        return nasRepository.search(value);
    }

    public Nas save(Nas nas) {
        Objects.requireNonNull(nas, Nas.EMPTY_NAS);

        CredentialsTranslation translation = nas.getTranslation();
        if (translation != null) {
            nas.setTranslation(translation);

            CredentialsEncoder encoder = translation.getEncoder();
            if (encoder != null) {
                credentialsEncoderRepository.save(translation.getEncoder());
            }

            if (!translation.isEmpty()) {
                translation.getModifiers().forEach(m -> credentialsModifierRepository.save(m));
            }

            credentialsTranslationRepository.save(translation);
        }

        return nasRepository.save(nas);
    }

    public void delete(String name) {
        nasRepository.delete(name);
    }
}
