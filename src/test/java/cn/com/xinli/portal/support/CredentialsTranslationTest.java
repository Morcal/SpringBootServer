package cn.com.xinli.portal.support;

import cn.com.xinli.portal.core.credentials.*;
import cn.com.xinli.portal.core.nas.HuaweiNas;
import cn.com.xinli.portal.transport.huawei.AuthType;
import cn.com.xinli.portal.util.CodecUtils;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
  * <p>Project: xpws
 *
 * @author zhoupeng 2016/1/1.
 */
public class CredentialsTranslationTest {
    /** Logger. */
    private final Logger logger = LoggerFactory.getLogger(CredentialsTranslationTest.class);

    final String privateKey = "some-private-key";
    final Credentials credentials = Credentials.of("foo", "bar", "192.168.3.26", "20-CF-30-BB-E9-AF");

    HuaweiNas nas;

    @Test
    public void testMd5Encoder() {
        CredentialsEncoder encoder = new CredentialsEncoders.Md5Encoder();
        Assert.assertNotNull(encoder);

        Credentials encoded = encoder.encode(credentials, privateKey);
        logger.debug("encoded credentials: {}", encoded);

        Assert.assertNotEquals(credentials.getPassword(), encoded.getPassword());

        final String encodedPassword = Hex.encodeHexString(CodecUtils.md5sum(credentials.getPassword().getBytes()));
        Assert.assertNotNull(encodedPassword);
        logger.debug("encoded password: {}", encodedPassword);

        Assert.assertEquals(encoded.getPassword(), encodedPassword);
    }

    @Test
    public void testSha1HexEncoder() {
        CredentialsEncoder encoder = new CredentialsEncoders.Sha1HexEncoder();
        Assert.assertNotNull(encoder);

        Credentials encoded = encoder.encode(credentials, privateKey);
        logger.debug("encoded credentials: {}", encoded);

        Assert.assertNotEquals(credentials.getPassword(), encoded.getPassword());

        final String encodedPassword = Hex.encodeHexString(CodecUtils.hmacSha1(credentials.getPassword().getBytes(), privateKey));
        Assert.assertNotNull(encodedPassword);
        logger.debug("encoded password: {}", encodedPassword);

        Assert.assertEquals(encoded.getPassword(), encodedPassword);
    }

    @Test
    public void testSha1Base64Encoder() {
        CredentialsEncoder encoder = new CredentialsEncoders.Sha1Base64Encoder();
        Assert.assertNotNull(encoder);

        Credentials encoded = encoder.encode(credentials, privateKey);
        logger.debug("encoded credentials: {}", encoded);

        Assert.assertNotEquals(credentials.getPassword(), encoded.getPassword());

        final String encodedPassword = Base64.encodeBase64String(CodecUtils.hmacSha1(credentials.getPassword().getBytes(), privateKey));
        Assert.assertNotNull(encodedPassword);
        logger.debug("encoded password: {}", encodedPassword);

        Assert.assertEquals(encoded.getPassword(), encodedPassword);
    }

    @Before
    public void createNas() {
        nas = new HuaweiNas();
        nas.setSharedSecret("aaa");
        nas.setName("test-01");
        nas.setIpv4Address("127.0.0.1");
        nas.setListenPort(2000);
        nas.setAuthType(AuthType.CHAP.name());
        nas.setId(1);

//        nas.setIpv4start("192.168.3.1");
//        nas.setIpv4end("192.168.3.254");
//        nas.setAuthenticateWithDomain(false);

        CredentialsTranslation translation = new CredentialsTranslation();
        nas.setTranslation(translation);

    }

//    @Test
//    public void testTranslationWithNoOp() {
//        CredentialsTranslation translation = CredentialsTranslations.getTranslation(nas);
//        Credentials translated = translation.translate(credentials);
//        Assert.assertNotNull(translated);
//        logger.debug("translated credentials: {}", translated);
//        Assert.assertEquals(credentials, translated);
//    }

    @Test
    public void testTranslationWithPrefixAndPostfix() {
        List<CredentialsModifier> modifiers = new ArrayList<>();

        CredentialsModifier prefix = new CredentialsModifier();
        prefix.setId(1L);
        prefix.setTarget(CredentialsModifier.Target.USERNAME);
        prefix.setPosition(CredentialsModifier.Position.HEAD);
        prefix.setValue("xl");

        CredentialsModifier postfix = new CredentialsModifier();
        postfix.setId(1L);
        postfix.setTarget(CredentialsModifier.Target.USERNAME);
        postfix.setPosition(CredentialsModifier.Position.TAIL);
        postfix.setValue("@xinli.com.cn");

        modifiers.add(prefix);
        modifiers.add(postfix);

        CredentialsTranslation translation = new CredentialsTranslation();
        translation.setModifiers(modifiers);

        nas.setTranslation(translation);
        Credentials translated = translation.translate(credentials);
        Assert.assertNotNull(translated);
        logger.debug("original credentials: {}", credentials);
        logger.debug("translated credentials: {}", translated);

        Assert.assertNotEquals(credentials.getUsername(), translated.getUsername());
    }
}
