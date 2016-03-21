package cn.com.xinli.scratch;

import org.apache.commons.codec.binary.Base64;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * <p>Project: xpws
 *
 * @author zhoupeng 2015/12/24.
 */
public class AESTest {
    /** Logger. */
    private final Logger logger = LoggerFactory.getLogger(AESTest.class);

    @Test
    public void testAES() throws NoSuchAlgorithmException, NoSuchPaddingException, UnsupportedEncodingException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        //final String content = "The quick brown fox jumps over a lazy dog.";
        final String content = "lip$123456";
//        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
//        AlgorithmParameterSpec spec =
//        keyGenerator.init(128, new SecureRandom("0123456789012345".getBytes()));
//        SecretKey secretKey = keyGenerator.generateKey();
//        byte[] enCodeFormat = secretKey.getEncoded();
//        SecretKeySpec key = new SecretKeySpec(enCodeFormat, "AES");
        SecretKeySpec key = new SecretKeySpec("1234567890123456".getBytes(), "AES");
        Cipher cipher = Cipher.getInstance("AES");// 创建密码器
        byte[] byteContent = content.getBytes("utf-8");
        cipher.init(Cipher.ENCRYPT_MODE, key);// 初始化
        byte[] result = cipher.doFinal(byteContent);
        logger.debug(Base64.encodeBase64String(result));


        MessageDigest md5 = MessageDigest.getInstance("MD5");
        md5.reset();
        md5.update(content.getBytes());
        result = md5.digest();

        String out = Base64.encodeBase64String(result);
        logger.debug(out);
    }
}
