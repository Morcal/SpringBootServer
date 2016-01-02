package cn.com.xinli.portal.util;

import cn.com.xinli.portal.AuthType;
import cn.com.xinli.portal.Credentials;
import cn.com.xinli.portal.CredentialsTranslation;
import cn.com.xinli.portal.NasType;
import cn.com.xinli.portal.persist.CredentialsModifierEntity;
import cn.com.xinli.portal.persist.CredentialsTranslationEntity;
import cn.com.xinli.portal.persist.NasEntity;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.security.crypto.codec.Hex;

import java.util.ArrayList;
import java.util.List;

/**
 * Project: xpws
 *
 * @author zhoupeng 2016/1/1.
 */
public class CredentialsTranslationTest {
    /** Logger. */
    private final Logger logger = LoggerFactory.getLogger(CredentialsTranslationTest.class);

    final Credentials credentials = new Credentials("foo", "bar", "192.168.3.26", "20-CF-30-BB-E9-AF");

    @Test
    public void testMd5Encoder() {
        CredentialsEncoder encoder = CredentialsEncoders.getEncoder("md5", null);
        Assert.assertNotNull(encoder);

        Credentials encoded = encoder.encode(credentials);
        logger.debug("encoded credentials: {}", encoded);

        Assert.assertNotEquals(credentials.getPassword(), encoded.getPassword());

        final String encodedPassword = new String(Hex.encode(CodecUtil.md5sum(credentials.getPassword().getBytes())));
        Assert.assertNotNull(encodedPassword);
        logger.debug("encoded password: {}", encodedPassword);

        Assert.assertEquals(encoded.getPassword(), encodedPassword);
    }

    @Test
    public void testSha1HexEncoder() {
        final String privateKey = "some-private-key";
        CredentialsEncoder encoder = CredentialsEncoders.getEncoder("sha1hex", privateKey);
        Assert.assertNotNull(encoder);

        Credentials encoded = encoder.encode(credentials);
        logger.debug("encoded credentials: {}", encoded);

        Assert.assertNotEquals(credentials.getPassword(), encoded.getPassword());

        final String encodedPassword = new String(Hex.encode(CodecUtil.hmacSha1(credentials.getPassword().getBytes(), privateKey)));
        Assert.assertNotNull(encodedPassword);
        logger.debug("encoded password: {}", encodedPassword);

        Assert.assertEquals(encoded.getPassword(), encodedPassword);
    }

    @Test
    public void testSha1Base64Encoder() {
        final String privateKey = "some-private-key";
        CredentialsEncoder encoder = CredentialsEncoders.getEncoder("sha1base64", privateKey);
        Assert.assertNotNull(encoder);

        Credentials encoded = encoder.encode(credentials);
        logger.debug("encoded credentials: {}", encoded);

        Assert.assertNotEquals(credentials.getPassword(), encoded.getPassword());

        final String encodedPassword = new String(Base64.encode(CodecUtil.hmacSha1(credentials.getPassword().getBytes(), privateKey)));
        Assert.assertNotNull(encodedPassword);
        logger.debug("encoded password: {}", encodedPassword);

        Assert.assertEquals(encoded.getPassword(), encodedPassword);
    }

    private NasEntity createNasEntity() {
        NasEntity entity = new NasEntity();
        entity.setType(NasType.HuaweiV2);
        entity.setSharedSecret("aaa");
        entity.setName("Test Nas");
        entity.setIpv4Address("127.0.0.1");
        entity.setListenPort(2000);
        entity.setAuthType(AuthType.CHAP);
        entity.setId(1);
        entity.setIpv4start(AddressUtil.convertIpv4Address("192.168.3.1"));
        entity.setIpv4end(AddressUtil.convertIpv4Address("192.168.3.254"));
        entity.setNasId("test-01");

        CredentialsTranslationEntity translationEntity = new CredentialsTranslationEntity();
        entity.setTranslation(translationEntity);

        return entity;
    }

    @Test
    public void testTranslationWithNoOp() {
        NasEntity entity = createNasEntity();
        CredentialsTranslation translation = CredentialsTranslations.getTranslation(entity);
        Credentials translated = translation.translate(credentials);
        Assert.assertNotNull(translated);
        logger.debug("translated credentials: {}", translated);
        Assert.assertEquals(credentials, translated);
    }

    @Test
    public void testTranslationWithPrefixAndPostfix() {
        NasEntity entity = createNasEntity();

        List<CredentialsModifierEntity> modifiers = new ArrayList<>();

        CredentialsModifierEntity prefix = new CredentialsModifierEntity();
        prefix.setId(1L);
        prefix.setTarget(CredentialsModifier.Target.USERNAME);
        prefix.setPosition(CredentialsModifier.Position.HEAD);
        prefix.setValue("xl");

        CredentialsModifierEntity postfix = new CredentialsModifierEntity();
        postfix.setId(1L);
        postfix.setTarget(CredentialsModifier.Target.USERNAME);
        postfix.setPosition(CredentialsModifier.Position.TAIL);
        postfix.setValue("@xinli.com.cn");

        modifiers.add(prefix);
        modifiers.add(postfix);

        entity.getTranslation().setModifiers(modifiers);
        CredentialsTranslation translation = CredentialsTranslations.getTranslation(entity);
        Credentials translated = translation.translate(credentials);
        Assert.assertNotNull(translated);
        logger.debug("translated credentials: {}", translated);

        Assert.assertNotEquals(credentials, translated);
    }
}
