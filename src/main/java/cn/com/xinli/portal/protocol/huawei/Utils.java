package cn.com.xinli.portal.protocol.huawei;

import cn.com.xinli.portal.Session;
import cn.com.xinli.portal.protocol.AuthType;
import cn.com.xinli.portal.protocol.Credentials;
import cn.com.xinli.portal.protocol.Packet;
import cn.com.xinli.portal.protocol.Protocol;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

/**
 * Project: xpws
 *
 * @author zhoupeng 2015/12/24.
 */
final class Utils {
    /** Log. */
    private static final Log log = LogFactory.getLog(Utils.class);

    static byte[] md5sum(byte[] data) {
        if (data == null)
            throw new IllegalArgumentException("md5 summary data can not be empty.");

        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.reset();
            md5.update(data);
            return md5.digest();
        } catch (NoSuchAlgorithmException e) {
            log.fatal(e);
            return new byte[0];
        }
    }

    /**
     * Get ipv4 address in bytes.
     * <p>
     * FIXME: can't apply on ipv6 address.
     *
     * @param ip ipv4 address in form of "xxx.xxx.xxx.xxx".
     * @return ipv4 address in bytes.
     * @throws UnknownHostException
     */
    static byte[] getIp4Address(String ip) throws UnknownHostException {
        byte[] bytes = InetAddress.getByName(ip).getAddress();
        if (bytes.length > 4) {
            return Arrays.copyOfRange(bytes, bytes.length - 4, bytes.length);
        } else {
            return Arrays.copyOf(bytes, 4);
        }
    }

    /**
     * Create Huawei portal CHAP authentiation packet.
     *
     * <p>
     * <code>CHAP password = MD5(CHAP id + Password + Challenge)</code>
     * </p>
     * @param ack   challenge acknowledge packet.
     * @param credentials user credentials.
     * @return Huawei portal packet.
     * @throws IOException
     */
    static Packet createChapAuthPacket(Protocol protocol, Packet ack, Credentials credentials) throws IOException {
        HuaweiPacket hwAck = (HuaweiPacket) ack;
        int attrs = 0;
        HuaweiPacket packet = new HuaweiPacket();
        packet.setVersion(protocol.getVersion());
        packet.setType(Enums.Type.REQ_AUTH.code());
        packet.setAuthType(AuthType.CHAP.code());
        packet.setSerialNum(hwAck.getSerialNum());
        packet.setReqId(hwAck.getReqId());
        packet.setIp(getIp4Address(credentials.getIp()));
        packet.addAttribute(Enums.Attribute.USER_NAME, credentials.getUsername().getBytes());
        attrs++;
        if (StringUtils.isEmpty(credentials.getMac())) {
            packet.addAttribute(Enums.Attribute.USER_MAC, credentials.getUsername().getBytes());
            attrs++;
        }

        /* Calculate chap password. */
        try (ByteArrayOutputStream bao = new ByteArrayOutputStream()) {
            DataOutputStream output = new DataOutputStream(bao);
            output.writeByte(packet.getReqId());
            output.flush();

            bao.write(credentials.getPassword().getBytes());
            bao.write(packet.getAttribute(Enums.Attribute.CHALLENGE));
            bao.flush();
            packet.addAttribute(Enums.Attribute.CHALLENGE_PASSWORD, md5sum(bao.toByteArray()));
            attrs++;
            packet.setAttrs(attrs);
        }

        return packet;
    }

    /**
     * Create challenge request packet.
     *
     * @param protocol protocol.
     * @param credentials user credentials.
     * @return challenge request packet.
     */
    static Packet createChapReqPacket(Protocol protocol, Credentials credentials) {
        try {
            int attrs = 0;
            HuaweiPacket packet = new HuaweiPacket();
            packet.setVersion(protocol.getVersion());
            packet.setType(Enums.Type.REQ_CHALLENGE.code());
            packet.setSerialNum(HuaweiPacket.nextSerialNum());
            packet.setAuthType(AuthType.CHAP.code());
            packet.setIp(getIp4Address(credentials.getIp()));
            if (!StringUtils.isEmpty(credentials.getMac())) {
                packet.addAttribute(Enums.Attribute.USER_MAC, credentials.getMac().getBytes());
                attrs++;
            }
            packet.setAttrs(attrs);
            return packet;
        } catch (UnknownHostException e) {
            log.error(e);
            return null;
        }
    }

    /**
     * Create PAP authentication packet.
     *
     * @param protocol protocol.
     * @param credentials user credentials.
     * @return PAP authentication packet.
     */
    static Packet createPapAuthPacket(Protocol protocol, Credentials credentials) throws IOException {
        int attrs = 0;
        HuaweiPacket packet = new HuaweiPacket();
        packet.setVersion(protocol.getVersion());
        packet.setType(Enums.Type.REQ_AUTH.code());
        packet.setSerialNum(HuaweiPacket.nextSerialNum());
        packet.setAuthType(AuthType.PAP.code());
        packet.setIp(getIp4Address(credentials.getIp()));
        packet.addAttribute(Enums.Attribute.USER_NAME, credentials.getUsername().getBytes());
        attrs++;
        packet.addAttribute(Enums.Attribute.PASSWORD, credentials.getPassword().getBytes());
        attrs++;
        if (StringUtils.isEmpty(credentials.getMac())) {
            packet.addAttribute(Enums.Attribute.USER_MAC, credentials.getUsername().getBytes());
            attrs++;
        }
        packet.setAttrs(attrs);

        return packet;
    }

    /**
     * Create logout request packet.
     *
     * @param protocol protocol.
     * @param authType    authentication type.
     * @param credentials user credentials.
     * @return logout request packet,
     * or null if ip address in credentials is unknown.
     */
    static Packet createLogoutPacket(Protocol protocol, AuthType authType, Credentials credentials) throws IOException {
        HuaweiPacket packet = new HuaweiPacket();
        packet.setVersion(protocol.getVersion());
        packet.setType(Enums.Type.REQ_LOGOUT.code());
        packet.setAuthType(authType.code());
        packet.setSerialNum(HuaweiPacket.nextSerialNum());
        packet.setReqId(0);
        packet.setIp(getIp4Address(credentials.getIp()));
        packet.setPort(0);
        packet.setError(Enums.LogoutRequestError.PORTAL_SERVER_REQUEST.code());
        packet.setAttrs(0);
        return packet;
    }

    /**
     * Create affirmative acknowledge packet.
     *
     * @param protocol protocol.
     * @param response response packet from remote.
     * @return packet.
     */
    static Packet createAffAckPacket(Protocol protocol, Packet response) {
        HuaweiPacket hwPkt = (HuaweiPacket) response, ack = new HuaweiPacket();
        ack.setVersion(protocol.getVersion());
        ack.setType(Enums.Type.AFF_ACK_AUTH.code());
        ack.setAuthType(hwPkt.getAuthType());
        ack.setReserved(0);
        ack.setSerialNum(hwPkt.getSerialNum());
        ack.setReqId(hwPkt.getReqId());
        ack.setIp(hwPkt.getIp());
        ack.setError(0);
        ack.setAttrs(0);
        return ack;
    }

    /**
     * Create request timeout packet.
     * <p>
     * NAK packet use {@link Enums.Type#REQ_LOGOUT} as packet type.
     * NAK (TIMEOUT) packet error code must be 1.
     * </p>
     * @param protocol protocol.
     * @param request original request.
     * @return request timeout packet.
     */
    static Packet createRequestTimeoutPacket(Protocol protocol, Packet request) {
        HuaweiPacket hwPkt = (HuaweiPacket) request, nak = new HuaweiPacket();
        nak.setVersion(protocol.getVersion());
        nak.setType(Enums.Type.REQ_LOGOUT.code());
        nak.setAuthType(hwPkt.getAuthType());
        nak.setReserved(0);
        nak.setSerialNum(hwPkt.getSerialNum());
        nak.setReqId(hwPkt.getReqId());
        nak.setIp(hwPkt.getIp());
        nak.setError(Enums.LogoutRequestError.REQUEST_TIMEOUT.code());
        nak.setAttrs(0);
        return nak;
    }

    /**
     * Create authentication response packet.
     * @param reqId request id.
     * @param authenticated if request authenticated.
     * @param request authentication rqeuest.
     * @return response packet.
     */
    static Packet createAuthenticationResponsePacket(int reqId, boolean authenticated, HuaweiPacket request) {
        HuaweiPacket response = new HuaweiPacket();

        response.setVersion(request.getVersion());
        response.setType(Enums.Type.ACK_AUTH.code());
        response.setAuthType(request.getAuthType());
        response.setReserved(request.getReserved());
        response.setSerialNum(request.getSerialNum());
        response.setReqId(reqId);
        response.setIp(request.getIp());
        response.setPort(request.getPort());
        response.setError(Enums.AuthError.OK.code());
        response.setAttrs(1);

        if (authenticated) {
            response.setError(Enums.AuthError.OK.code());
            response.addAttribute(Enums.Attribute.TEXT_INFO, "Authentication result: OK.".getBytes());
        } else {
            response.setVersion(request.getVersion());
            response.setError(Enums.AuthError.REJECTED.code());
            response.addAttribute(Enums.Attribute.TEXT_INFO, "Authentication result: REJECTED.".getBytes());
        }
        return response;
    }

    /**
     * Create challenge response packet.
     * @param nasAddress nas address.
     * @param challenge challenge.
     * @param reqId challenge request id.
     * @param request challenge request.
     * @return packet.
     */
    static Packet createChallengeResponsePacket(InetAddress nasAddress, String challenge, int reqId, HuaweiPacket request) {
        HuaweiPacket response = new HuaweiPacket();
        response.setVersion(request.getVersion());
        response.setType(Enums.Type.ACK_CHALLENGE.code());
        response.setAuthType(request.getAuthType());
        response.setSerialNum(request.getSerialNum());
        response.setReqId(reqId);
        response.setError(Enums.AuthError.OK.code());
        response.setIp(request.getIp());
        response.setPort(request.getPort());
        response.setReserved(request.getReserved());
        response.addAttribute(Enums.Attribute.CHALLENGE, challenge.getBytes());
        response.addAttribute(Enums.Attribute.BAS_IP, nasAddress.getAddress());
        response.setAttrs(2);

        return response;
    }

    /**
     * Craete logout response packet.
     * @param nasAddress nas address.
     * @param session session.
     * @param request logout request.
     * @return packet.
     */
    static Packet createLogoutResponsePacket(InetAddress nasAddress, Session session, HuaweiPacket request) {
        HuaweiPacket response = new HuaweiPacket();
        response.setVersion(request.getVersion());
        response.setType(Enums.Type.ACK_LOGOUT.code());
        response.setAuthType(request.getAuthType());
        response.setSerialNum(request.getSerialNum());
        response.setReqId(request.getReqId());
        if (session == null) {
            response.setError(Enums.LogoutError.GONE.code());
        } else {
            response.setError(Enums.LogoutError.OK.code());
        }
        response.setIp(request.getIp());
        response.setPort(request.getPort());
        response.setReserved(request.getReserved());
        response.addAttribute(Enums.Attribute.BAS_IP, nasAddress.getAddress());
        response.setAttrs(1);

        return response;
    }
}
