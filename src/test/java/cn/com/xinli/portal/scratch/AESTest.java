package cn.com.xinli.portal.scratch;

import cn.com.xinli.portal.TestBase;
import org.junit.Test;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;

/**
 * Project: xpws
 *
 * @author zhoupeng 2015/12/24.
 */
public class AESTest extends TestBase {

    @Test
    public void testAES() throws NoSuchAlgorithmException, NoSuchPaddingException, UnsupportedEncodingException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        String content = "The quick brown fox jumps over a lazy dog.";
        KeyGenerator kgen = KeyGenerator.getInstance("AES");
        kgen.init(128, new SecureRandom("0123456789012345".getBytes()));
        SecretKey secretKey = kgen.generateKey();
        byte[] enCodeFormat = secretKey.getEncoded();
        SecretKeySpec key = new SecretKeySpec(enCodeFormat, "AES");
        Cipher cipher = Cipher.getInstance("AES");// 创建密码器
        byte[] byteContent = content.getBytes("utf-8");
        cipher.init(Cipher.ENCRYPT_MODE, key);// 初始化
        byte[] result = cipher.doFinal(byteContent);
        logger.debug(Arrays.toString(result));

        MessageDigest md5 = MessageDigest.getInstance("MD5");
        md5.reset();
        md5.update(content.getBytes());
        result = md5.digest();
        logger.debug(Arrays.toString(result));
    }
}
