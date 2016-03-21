package cn.com.xinli.portal.transport.huawei;

import cn.com.xinli.portal.core.credentials.Credentials;
import cn.com.xinli.portal.transport.TransportException;
import cn.com.xinli.portal.transport.TransportUtils;
import cn.com.xinli.portal.transport.huawei.nio.ByteBufferCodecFactory;
import cn.com.xinli.portal.transport.huawei.nio.DatagramConnector;
import cn.com.xinli.portal.transport.huawei.support.HuaweiPortal;
import cn.com.xinli.portal.util.CodecUtils;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * HUAWEI Packet Test.
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2015/12/24.
 */
public class PacketTest {
    /** Logger. */
    private final Logger logger = LoggerFactory.getLogger(PacketTest.class);

    Endpoint endpoint;
    Credentials credentials;
    ByteBufferCodecFactory codecFactory;

    String byteBufferToHexString(ByteBuffer buffer) {
        return TransportUtils.bytesToHexString(Arrays.copyOfRange(buffer.array(), 0, buffer.remaining()));
    }

    @Before
    public void setup() throws UnknownHostException {
        credentials = Credentials.of("zhoup", "123456", "192.168.3.26", "20cf-30bb-e9af");
        endpoint = new Endpoint();
        endpoint.setVersion(Version.V2);
        endpoint.setAddress(InetAddress.getByName("127.0.0.1"));
        endpoint.setPort(2000);
        endpoint.setSharedSecret("aaa");
        logger.debug("endpoint: {}", endpoint);
        codecFactory = new ByteBufferCodecFactory();
    }

    @Test
    public void testPapAuth() throws IOException, TransportException {
        endpoint.setAuthType(AuthType.PAP);
        DatagramConnector client = (DatagramConnector) HuaweiPortal.getConnector(endpoint);
        //Packet papAuth = client.createPapAuthPacket(credentials);
        Packet papAuth = client.createRequest(RequestType.REQ_AUTH, credentials, null, 1);
        ByteBuffer buffer = codecFactory.getEncoder().encode(papAuth, endpoint.getSharedSecret());
        logger.debug("PAP auth buffer: {}", byteBufferToHexString(buffer));
        buffer.rewind();
        Packet decoded = codecFactory.getDecoder().decode(buffer, endpoint.getSharedSecret());

        Assert.assertNotNull(decoded);
        Assert.assertTrue(Arrays.equals(papAuth.getAuthenticator(), decoded.getAuthenticator()));

        Assert.assertEquals(papAuth.toString(), decoded.toString());
        logger.debug("decoded: {}", decoded);
    }

    @Test
    public void testChapAuth() throws IOException, TransportException {
        endpoint.setAuthType(AuthType.CHAP);
        DatagramConnector client = (DatagramConnector) HuaweiPortal.getConnector(endpoint);
        Packet chapReq = Packets.newChapReq(Version.V2, credentials, 1);
        Packet chapAck = Packets.newChallengeAck(
                InetAddress.getLocalHost(), "challenge-value".getBytes(), 1, ChallengeError.OK, chapReq);
        Packet chapAuth = client.createRequest(RequestType.REQ_AUTH, credentials, chapAck, null, 1);
        ByteBuffer buffer = codecFactory.getEncoder().encode(chapAuth, endpoint.getSharedSecret());

        logger.debug("CHAP auth buffer: {}", byteBufferToHexString(buffer));
        buffer.rewind();
        Packet decoded = codecFactory.getDecoder().decode(buffer, endpoint.getSharedSecret());

        Assert.assertNotNull(decoded);
        Assert.assertTrue(Arrays.equals(chapAuth.getAuthenticator(), decoded.getAuthenticator()));

        Assert.assertEquals(chapAuth.toString(), decoded.toString());
        logger.debug("decoded: {}", decoded);
    }

    @Test
    public void testChallengePacket() throws IOException {
        final String sharedSecret = "1234567890123456";
        final Credentials credentials = Credentials.of("web@default0", "123456", "172.75.100.253", "");
        Packet challenge = Packets.newChapReq(Version.V2, credentials, 4);
        ByteBuffer buffer = codecFactory.getEncoder().encode(challenge, sharedSecret);

        final String result = byteBufferToHexString(buffer);
        logger.debug("result: {}", result);

        Assert.assertEquals(
                "02 01 00 00 00 04 00 00 ac 4b 64 fd 00 00 00 00 " +
                "e8 0d a1 b2 79 f3 b5 f2 e8 cb c2 dd 32 4f 56 dd", result);
    }

    @Test
    public void testChallengeAckMd5() throws DecoderException {
        final String in = "02 02 00 00 00 04 00 02 ac 4b 64 fd 00 00 00 01 e8 0d a1 b2 79 f3 b5 f2 e8 cb c2 dd 32 4f 56 dd 03 12 bb 0b cd 57 41 5d 3d b7 b7 cd 5b 39 3f c1 29 e3".replace(" ", "").trim();
        final String sharedSecret = "1234567890123456";
        final byte[] i = Hex.decodeHex(in.toCharArray());
        byte[] content  = new byte[i.length + sharedSecret.getBytes().length];
        System.arraycopy(i, 0, content, 0, i.length);
        System.arraycopy(sharedSecret.getBytes(), 0, content, i.length, sharedSecret.getBytes().length);
        final byte[] r = Packets.md5sum(content);

        final String rs = TransportUtils.bytesToHexString(r);
        logger.debug("result: {}", rs);
        Assert.assertEquals("4e 1f f4 eb 21 57 50 bc 1d 4a a4 e4 8b 25 76 11", rs);
    }

    @Test
    public void testChallengeAckPacket() throws IOException, DecoderException {
        final String cbs = "bb 0b cd 57 41 5d 3d b7 b7 cd 5b 39 3f c1 29 e3".replace(" ", "").trim();
        final byte[] chb = Hex.decodeHex(cbs.toCharArray());
        final String s = TransportUtils.bytesToHexString(chb);
        logger.debug("challenge: {}", s);
        final String sharedSecret = "1234567890123456";
        final Credentials credentials = Credentials.of("web@default0", "123456", "172.75.100.253", "");
        Packet req = Packets.newChapReq(Version.V2, credentials, 4);
        codecFactory.getEncoder().encode(req, sharedSecret);

        // request authenticator: e8 0d a1 b2 79 f3 b5 f2 e8 cb c2 dd 32 4f 56 dd
        assert req != null;
        final byte[] authenticator = req.getAuthenticator();
        logger.debug("request authenticator: {}", TransportUtils.bytesToHexString(authenticator));

        Packet ack = Packets.newChallengeAck(
                InetAddress.getByName(credentials.getIp()),
                chb,
                2,
                ChallengeError.OK,
                req);
        //.Version.V2, credentials, 4);
        ByteBuffer buffer = codecFactory.getEncoder().encode(authenticator, ack, sharedSecret);

        final String result = byteBufferToHexString(buffer);
        logger.debug("result: {}", result);

        Assert.assertEquals(
                "02 02 00 00 00 04 00 02 ac 4b 64 fd 00 00 00 01 " +
                "4e 1f f4 eb 21 57 50 bc 1d 4a a4 e4 8b 25 76 11 " +
                "03 12 bb 0b cd 57 41 5d 3d b7 b7 cd 5b 39 3f c1 " +
                "29 e3",
                result);
    }

    @Test
    public void testChapAuthPacket() throws IOException, DecoderException {
        final String sharedSecret = "1234567890123456";
        final Credentials credentials = Credentials.of("web@default0", "123456", "172.75.100.253", "");
        final String cbs = "bb 0b cd 57 41 5d 3d b7 b7 cd 5b 39 3f c1 29 e3".replace(" ", "").trim();
        final byte[] chb = Hex.decodeHex(cbs.toCharArray());
        final String s = TransportUtils.bytesToHexString(chb);
        logger.debug("challenge: {}", s);

        Packet req = Packets.newChapReq(Version.V2, credentials, 4);
        logger.debug("challenge request: {}", req);

        Packet ack = Packets.newChallengeAck(
                InetAddress.getByName(credentials.getIp()),
                chb,
                2,
                ChallengeError.OK,
                req);
        logger.debug("challenge ack: {}", ack);

        Packet auth = Packets.newChapAuth(Version.V2, ack, credentials, 4);
        logger.debug("chap auth: {}", auth);

        ByteBuffer buffer = codecFactory.getEncoder().encode(auth, sharedSecret);

        final String result = byteBufferToHexString(buffer);
        logger.debug("result: {}", result);

        Assert.assertEquals(
                "02 03 00 00 00 04 00 02 ac 4b 64 fd 00 00 00 02 " +
                "30 7b ba a5 26 5d f5 28 e7 33 94 97 2c 15 1b 69 " +
                "04 12 73 cc 87 6a 01 ca 9a b3 98 d7 fc c8 58 36 " +
                "b5 8b 01 0e 77 65 62 40 64 65 66 61 75 6c 74 30",
                result);
    }
}
