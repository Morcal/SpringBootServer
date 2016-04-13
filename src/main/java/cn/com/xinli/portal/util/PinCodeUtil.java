package cn.com.xinli.portal.util;

import cn.com.xinli.portal.core.PortalError;
import cn.com.xinli.portal.core.ServerException;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

/**
 * PIN Code utility.
 *
 * @author zhoupeng, created on 2016/4/13.
 */
public class PinCodeUtil {
    /** Logger. */
    private static final Logger logger = LoggerFactory.getLogger(PinCodeUtil.class);

    /** Digest salt. */
    private static final String SALT
            = "cg9ms6oj8ehl7qfd4iz0N5RUP2KAT3XBWV1TCGMSOJEHLQFDIZnrupkatxbwvt";

    /** Digest salt length. */
    private static final int SALT_LENGTH = SALT.length();

    /**
     * Create hash.
     * @param key key.
     * @return hash.
     */
    private static String createHash(String key) {
        if (StringUtils.isEmpty(key)) {
            throw new IllegalArgumentException("Hash key can not be empty.");
        }

        String k = StringUtils.left(key, 16);
        StringBuilder builder = new StringBuilder(k);

        for (int i = k.length(); i < 32; i++) {
            int nIndex = SALT.charAt(i - k.length()) % SALT_LENGTH;
            builder.append(SALT.charAt(nIndex));
        }

        return builder.toString();
    }


    /**
     * Convert long integer to 4 bytes.
     *
     * @param v long integer.
     * @return 4 bytes.
     */
    private static byte[] longToBytes(long v) {
        // return Integer.toHexString((int)l).getBytes();
        byte[] localtime = new byte[4];
        localtime[0] = (byte) (((v >>> 24) & 0xFF));
        localtime[1] = (byte) (((v >>> 16) & 0xFF));
        localtime[2] = (byte) (((v >>> 8) & 0xFF));
        localtime[3] = (byte) (((v) & 0xFF));
        return localtime;
    }

    /**
     * Natural sequence to swap sequence.
     *
     * <p>将自然序列(BigIndian)转换为特殊序列 格式为
     * 31 30 29 28 27 26 25 24 23 22 21 20 19 18 17 16
     * 15 14 13 12 11 10  9  8  7  6  5  4  3  2  1  0
     * 0  4  8 12 16 20 24 28  1  5  9 13 17 21 25 29
     * 2  6 10 14 18 22 26 30  3  7 11 15 19 23 27 31
     *
     * @param in input bytes.
     * @return swap sequence bytes.
     */
    private static byte[] naturalSeqToSwapSeq(byte[] in) {
        byte[] out = new byte[4];
        for (int i = 0, j = 3, k = 0; i < 32; i++, j = (3 - (i / 8)), k = (i % 4)) {
            out[k] = (byte) (out[k] << 1);
            out[k] |= (in[j] & 0x1);
            in[j] = (byte) ((in[j]) >>> 1);
        }

        if (logger.isDebugEnabled()) {
            logger.debug("natural sequence: {}", Hex.encodeHexString(out));
        }

        return out;
    }

    /**
     * Generate time tamp based hash.
     * @return hash in form of byte 6.
     */
    private static byte[] generateHashBasedOnTimeStamp(long timestamp) {
        long base = timestamp / 5L;
        return Special4To6(naturalSeqToSwapSeq(longToBytes(base)));
    }

    /**
     * Generate PIN.
     * @param data data.
     * @param key key.
     * @return PIN code in string.
     * @throws ServerException
     */
    public static String generatePIN(String data, String key, long timestamp) throws ServerException {
        if (StringUtils.isEmpty(data) || StringUtils.isEmpty(key)) {
            throw new IllegalArgumentException("data or key is invalid.");
        }

        final String hash = createHash(key);
        final byte[] byte6 = generateHashBasedOnTimeStamp(timestamp);

        if (logger.isDebugEnabled()) {
            logger.debug("hash: {}", hash);
            logger.debug("time hash: {}", Hex.encodeHex(byte6));
        }

        return generatePIN(data, hash, byte6);
    }

    /**
     * Generate PIN.
     * @param data data.
     * @param hash hash.
     * @param byte6 byte 6.
     * @return PIN.
     * @throws ServerException
     */
    private static String generatePIN(String data, String hash, byte[] byte6) throws ServerException {
        try {
            byte[] digest = md5Digest(data, hash, byte6);
            int i0 = (digest[6] & 0x0f);
            int i1 = (digest[11] & 0xf0) >> 4;
            int i2 = (digest[13] & 0x03c) >> 2;

            byte c1 = digest[i2];
            byte c0 = digest[i1];
            byte c2 = digest[i0];

            byte check = (byte) (((c2 & 0xf) << 6) + ((c0 & 0xf0) >> 6) + (c1 & 0x3c));

            return String.format("%6s%02x", new String(byte6, "ASCII"), check);
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            throw new ServerException(PortalError.SERVER_INTERNAL_ERROR, "failed to generate pin", e);
        }
    }

    /**
     * Calculate MD5 digest.
     * @param data data.
     * @param hash hash.
     * @param byte6 byte6.
     * @return MD5 summary.
     * @throws NoSuchAlgorithmException
     * @throws UnsupportedEncodingException
     */
    private static byte[] md5Digest(String data, String hash, byte[] byte6)  throws NoSuchAlgorithmException,
            UnsupportedEncodingException {

        MessageDigest md5 = MessageDigest.getInstance("MD5");

        int nameLen = data.length(), nlen = 0;

        int flagLen = 6, flen = 0;
        int pskLen = 32, plen = 0;

        byte[] buffer = new byte[96];
        for (int i = 0; i < 64; i++) {
            if (i % 3 == 0 && nlen < nameLen) {
                buffer[i] = (byte) data.charAt(nlen);
                nlen++;
                continue;
            } else if (i % 3 == 1 && flen < flagLen) {
                buffer[i] = byte6[flen];
                flen++;
                continue;
            } else if (i % 3 == 2 && plen < pskLen) {
                buffer[i] = (byte) hash.charAt(plen);
                plen++;
                continue;
            }

            if (flen < flagLen) {
                buffer[i] = byte6[flen];
                flen++;
                continue;
            }

            if (plen < pskLen) {
                buffer[i] = (byte) hash.charAt(plen);
                plen++;
                continue;
            }

            buffer[i] = (byte) i;
        }

        md5.update(Arrays.copyOf(buffer, 64));

        return md5.digest();
    }

    /**
     * Special byte 4 to byte 6.
     * @param byte4 input byte 4.
     * @return byte 6.
     */
    private static byte[] Special4To6(byte[] byte4) {
        byte[] byte6 = new byte[6];
        byte6[0] = (byte) ((byte4[0] & 0xff) >>> 2);

        byte6[1] = (byte) ((byte4[0] & 0x3) << 4);
        byte6[1] |= ((byte4[1] & 0xff) >>> 4);

        byte6[2] = (byte) ((byte4[1] & 0xf) << 2);
        byte6[2] |= (byte4[2] & 0xff) >>> 6;

        byte6[3] = (byte) ((byte4[2] & 0x3f));

        byte6[4] = (byte) (((byte4[3] & 0XFF) >>> 2));
        byte6[5] = (byte) ((byte4[3] & 0x3) << 4);

        for (int i = 0; i < 6; i++) {
            byte6[i] += 0x20;
            if ((byte6[i] & 0xFF) >= 0x40) {
                // 跳过@符号
                byte6[i] += 1;
            }
        }

        return byte6;
    }

//    /**
//     * Special byte 6 to byte 4.
//     * @param byte6 input byte 6.
//     * @return byte 4.
//     */
//    private static byte[] Special6To4(byte[] byte6) {
//        byte[] byte4 = new byte[4];
//
//        for (int i = 0; i < 6; i++) {
//            if (byte6[i] > 0x40) {
//                byte6[i] -= 1;
//            }
//            byte6[i] -= 0x20;
//        }
//
//        byte4[0] = (byte) (byte6[0] << 2);
//        byte4[0] |= (((byte6[1] & 0xFF) >>> 4) & 0xf);
//
//        byte4[1] = (byte) (byte6[1] << 4);
//        byte4[1] |= (byte) ((((byte6[2] & 0xff) >>> 2) & 0xf));
//
//        byte4[2] = (byte) (byte6[2] << 6);
//        byte4[2] |= byte6[3] & 0x3f;
//
//        byte4[3] = (byte) (byte6[4] << 2);
//        byte4[3] |= (((byte6[5] & 0xff) >>> 4) & 3);
//
//        return byte4;
//    }
}
