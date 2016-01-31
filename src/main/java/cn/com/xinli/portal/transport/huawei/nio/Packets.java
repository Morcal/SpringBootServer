package cn.com.xinli.portal.transport.huawei.nio;

import cn.com.xinli.portal.core.credentials.Credentials;
import cn.com.xinli.portal.transport.huawei.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;

/**
 * HUAWEI portal packet helper.
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2015/12/24.
 */
final class Packets {
    /** Logger. */
    private static Logger logger = LoggerFactory.getLogger(Packets.class);

    /**
     * Calculate MD5 summary.
     *
     * @param data data to calculate.
     * @return calculated bytes.
     */
    static byte[] md5sum(byte[] data) {
        if (data == null)
            throw new IllegalArgumentException("md5 summary data can not be empty.");

        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.reset();
            md5.update(data);
            return md5.digest();
        } catch (NoSuchAlgorithmException e) {
            logger.error("Missing MD5", e);
            return new byte[0];
        }
    }

    /**
     * Create CHAP password.
     *
     * <p><code>CHAP password = MD5(reqId & 0xFF + password + challenge)</code>
     *
     * @param reqId     original request id.
     * @param password  password in plain text (ASCII).
     * @param challenge challenge.
     * @return CHAP password in bytes.
     */
    static byte[] newChapPassword(int reqId, String password, byte[] challenge) {
        ByteBuffer buffer = ByteBuffer.allocate(HuaweiPacket.MAX_LENGTH);
        buffer.put((byte) (reqId & 0xFF));
        if (!StringUtils.isEmpty(password)) {
            buffer.put(password.getBytes());
        }
        buffer.put(challenge);
        buffer.flip();
        return md5sum(buffer.array());
    }

    /**
     * Get ipv4 address in bytes.
     *
     * <p>FIXME: can't apply on ipv6 address.
     *
     * @param ip ipv4 address in form of "xxx.xxx.xxx.xxx".
     * @return ipv4 address in bytes.
     * @throws UnknownHostException
     */
    static byte[] getIp4Address(String ip) throws IOException {
        byte[] bytes = InetAddress.getByName(ip).getAddress();
        if (bytes.length > 4) {
            return Arrays.copyOfRange(bytes, bytes.length - 4, bytes.length);
        } else {
            return Arrays.copyOf(bytes, 4);
        }
    }

    /**
     * Create HUAWEI portal CHAP authentication packet.
     *
     * <p><code>CHAP password = MD5(CHAP id + Password + Challenge)</code>
     *
     * @param version    protocol version.
     * @param ack         challenge acknowledge packet.
     * @param credentials user credentials.
     * @return HUAWEI portal packet.
     * @throws IOException
     */
    static HuaweiPacket newChapAuth(Version version,
                                    HuaweiPacket ack,
                                    Credentials credentials) throws IOException {
        Objects.requireNonNull(ack);
        int attrs = 0;
        HuaweiPacket packet = new HuaweiPacket();
        packet.setVersion(version.value());
        packet.setType(RequestType.REQ_AUTH.code());
        packet.setAuthType(AuthType.CHAP.code());
        packet.setSerialNum(ack.getSerialNum());
        packet.setReqId(ack.getReqId());
        packet.setIp(getIp4Address(credentials.getIp()));
        packet.addAttribute(AttributeType.USER_NAME, credentials.getUsername().getBytes());
        attrs++;
        if (StringUtils.isEmpty(credentials.getMac())) {
            packet.addAttribute(AttributeType.USER_MAC, credentials.getUsername().getBytes());
            attrs++;
        }

        /* Calculate chap password. */
        packet.addAttribute(AttributeType.CHALLENGE_PASSWORD,
                newChapPassword(
                        ack.getReqId(),
                        credentials.getPassword(),
                        ack.getAttribute(AttributeType.CHALLENGE)));
        attrs++;
        packet.setAttrs(attrs);

        return packet;
    }

    /**
     * Create challenge request packet.
     *
     * @param version    protocol version.
     * @param credentials user credentials.
     * @return challenge request packet.
     * @throws IOException
     */
    static HuaweiPacket newChapReq(Version version, Credentials credentials) throws IOException {
        try {
            int attrs = 0;
            HuaweiPacket packet = new HuaweiPacket();
            packet.setVersion(version.value());
            packet.setType(RequestType.REQ_CHALLENGE.code());
            packet.setSerialNum(HuaweiPacket.nextSerialNum());
            packet.setAuthType(AuthType.CHAP.code());
            packet.setIp(getIp4Address(credentials.getIp()));
            if (!StringUtils.isEmpty(credentials.getMac())) {
                packet.addAttribute(AttributeType.USER_MAC, credentials.getMac().getBytes());
                attrs++;
            }
            packet.setAttrs(attrs);
            return packet;
        } catch (UnknownHostException e) {
            logger.error("Unknown host", e);
            return null;
        }
    }

    /**
     * Create PAP authentication packet.
     *
     * @param version    protocol version.
     * @param credentials user credentials.
     * @return PAP authentication packet.
     * @throws IOException
     */
    static HuaweiPacket newPapAuth(Version version, Credentials credentials) throws IOException {
        int attrs = 0;
        HuaweiPacket packet = new HuaweiPacket();
        packet.setVersion(version.value());
        packet.setType(RequestType.REQ_AUTH.code());
        packet.setSerialNum(HuaweiPacket.nextSerialNum());
        packet.setAuthType(AuthType.PAP.code());
        packet.setIp(getIp4Address(credentials.getIp()));
        packet.addAttribute(AttributeType.USER_NAME, credentials.getUsername().getBytes());
        attrs++;
        packet.addAttribute(AttributeType.PASSWORD, credentials.getPassword().getBytes());
        attrs++;
        if (StringUtils.isEmpty(credentials.getMac())) {
            packet.addAttribute(AttributeType.USER_MAC, credentials.getUsername().getBytes());
            attrs++;
        }
        packet.setAttrs(attrs);

        return packet;
    }

    /**
     * Create logout request packet.
     *
     * @param version    protocol version.
     * @param authType    authentication type.
     * @param credentials user credentials.
     * @return logout request packet,
     * or null if ip address in credentials is unknown.
     * @throws IOException
     */
    static HuaweiPacket newLogout(Version version,
                                  AuthType authType,
                                  Credentials credentials) throws IOException {
        HuaweiPacket packet = new HuaweiPacket();
        packet.setVersion(version.value());
        packet.setType(RequestType.REQ_LOGOUT.code());
        packet.setAuthType(authType.code());
        packet.setSerialNum(HuaweiPacket.nextSerialNum());
        packet.setReqId(0);
        packet.setIp(getIp4Address(credentials.getIp()));
        packet.setPort(0);
        packet.setError(LogoutRequestError.REQUEST.code());
        packet.setAttrs(0);
        return packet;
    }

    /**
     * Create affirmative acknowledge packet.
     *
     * @param version    protocol version.
     * @param response response packet from remote.
     * @return packet.
     */
    static HuaweiPacket newAffAck(Version version, HuaweiPacket response) {
        Objects.requireNonNull(response);
        HuaweiPacket ack = new HuaweiPacket();
        ack.setVersion(version.value());
        ack.setType(RequestType.AFF_ACK_AUTH.code());
        ack.setAuthType(response.getAuthType());
        ack.setReserved(0);
        ack.setSerialNum(response.getSerialNum());
        ack.setReqId(response.getReqId());
        ack.setIp(response.getIp());
        ack.setError(0);
        ack.setAttrs(0);
        return ack;
    }

    /**
     * Create request timeout packet.
     *
     * <p>NAK packet use {@link RequestType#REQ_LOGOUT} as packet type.
     * NAK (TIMEOUT) packet error code must be 1.
     *
     * @param version    protocol version.
     * @param request  original request.
     * @return request timeout packet.
     */
    static HuaweiPacket newTimeout(Version version, HuaweiPacket request) {
        Objects.requireNonNull(request);
        HuaweiPacket nak = new HuaweiPacket();
        nak.setVersion(version.value());
        nak.setType(RequestType.REQ_LOGOUT.code());
        nak.setAuthType(request.getAuthType());
        nak.setReserved(0);
        nak.setSerialNum(request.getSerialNum());
        nak.setReqId(request.getReqId());
        nak.setIp(request.getIp());
        nak.setError(LogoutRequestError.REQUEST_TIMEOUT.code());
        nak.setAttrs(0);
        return nak;
    }

    /**
     * Create authentication ack response packet.
     *
     * @param nasAddress nas address.
     * @param reqId         request id.
     * @param error authentication error.
     * @param request       authentication request.
     * @return response packet.
     */
    static HuaweiPacket newAuthAck(InetAddress nasAddress,
                                   int reqId,
                                   AuthError error,
                                   HuaweiPacket request) {
        Objects.requireNonNull(request);
        HuaweiPacket response = new HuaweiPacket();

        response.setVersion(request.getVersion());
        response.setType(RequestType.ACK_AUTH.code());
        response.setAuthType(request.getAuthType());
        response.setReserved(request.getReserved());
        response.setSerialNum(request.getSerialNum());
        response.setReqId(reqId);
        response.setIp(request.getIp());
        response.setPort(request.getPort());
        response.setError(AuthError.OK.code());
        response.setError(error.code());
        response.setAttrs(2);
        response.addAttribute(AttributeType.BAS_IP, nasAddress.getAddress());
        response.addAttribute(AttributeType.TEXT_INFO,
                ("Authentication result: " + error.getDescription()).getBytes());

        return response;
    }

    /**
     * Create challenge response packet.
     *
     * <p>H3C vBRAS will response with a bas ip attribute.
     *
     * @param nasAddress nas address.
     * @param challenge  challenge.
     * @param reqId      challenge request id.
     * @param error      challenge error.
     * @param request    challenge request.
     * @return packet.
     */
    static HuaweiPacket newChallengeAck(InetAddress nasAddress,
                                        String challenge,
                                        int reqId,
                                        ChallengeError error,
                                        HuaweiPacket request) {
        Objects.requireNonNull(request);
        HuaweiPacket response = new HuaweiPacket();
        response.setVersion(request.getVersion());
        response.setType(RequestType.ACK_CHALLENGE.code());
        response.setAuthType(request.getAuthType());
        response.setSerialNum(request.getSerialNum());
        response.setReqId(reqId);
        response.setError(AuthError.OK.code());
        response.setIp(request.getIp());
        response.setPort(request.getPort());
        response.setReserved(request.getReserved());
        response.setError(error.code());
        response.setAttrs(2);
        response.addAttribute(AttributeType.CHALLENGE, challenge.getBytes());
        response.addAttribute(AttributeType.BAS_IP, nasAddress.getAddress());

        return response;
    }

    /**
     * Create logout response packet.
     *
     * @param nasAddress nas address.
     * @param error    logout error.
     * @param request    logout request.
     * @return packet.
     */
    static HuaweiPacket newLogoutAck(InetAddress nasAddress,
                                     LogoutError error,
                                     HuaweiPacket request) {
        Objects.requireNonNull(request);
        HuaweiPacket response = new HuaweiPacket();
        response.setVersion(request.getVersion());
        response.setType(RequestType.ACK_LOGOUT.code());
        response.setAuthType(request.getAuthType());
        response.setSerialNum(request.getSerialNum());
        response.setReqId(request.getReqId());
        response.setIp(request.getIp());
        response.setPort(request.getPort());
        response.setReserved(request.getReserved());
        response.setError(error.code());
        response.setAttrs(1);
        response.addAttribute(AttributeType.BAS_IP, nasAddress.getAddress());

        return response;
    }

    /**
     * Create NTF_LOGOUT request packet.
     *
     * @param version    protocol version.
     * @param authType authentication type.
     * @param nasAddress NAS address.
     * @param ip user ip address.
     * @param reqId request id.
     * @return packet.
     */
    static HuaweiPacket newNtfLogout(Version version, AuthType authType,
                                     InetAddress nasAddress, byte[] ip, int reqId) {
        //LogoutRequestError
        HuaweiPacket ntf = new HuaweiPacket();
        ntf.setVersion(version.value());
        ntf.setType(RequestType.ACK_LOGOUT.code());
        ntf.setAuthType(authType.code());
        ntf.setSerialNum(0);
        ntf.setReqId(reqId);
        ntf.setIp(ip);
        ntf.setPort(0);
        ntf.setReserved(0);
        ntf.setError(LogoutRequestError.REQUEST.code());
        ntf.setAttrs(1);
        ntf.addAttribute(AttributeType.BAS_IP, nasAddress.getAddress());
        return ntf;
    }

    /**
     * Create NTF_LOGOUT request response ACK packet.
     * @param request original request.
     * @param error logout error.
     * @return packet.
     */
    static HuaweiPacket newNtfLogoutAck(HuaweiPacket request, LogoutError error) {
        Objects.requireNonNull(request);
        //LogoutRequestError
        HuaweiPacket ntf = new HuaweiPacket();
        ntf.setVersion(request.getVersion());
        ntf.setType(RequestType.ACK_NTF_LOGOUT.code());
        ntf.setAuthType(request.getAuthType());
        ntf.setSerialNum(0);
        ntf.setReqId(request.getReqId());
        ntf.setIp(request.getIp());
        ntf.setPort(request.getPort());
        ntf.setReserved(request.getReserved());
        ntf.setError(error.code());
        return ntf;
    }

    /**
     * Builder text content from HUAWEI portal packet.
     * @return text content.
     */
    static String buildText(HuaweiPacket packet) {
        Objects.requireNonNull(packet);

        try {
            Optional<RequestType> type = RequestType.valueOf(packet.getType());

            int error = packet.getError();

            if (type.isPresent()) {
                switch (type.get()) {
                    case ACK_AUTH:
                        return AuthError.valueOf(error).get().getDescription();

                    case ACK_LOGOUT:
                        return LogoutError.valueOf(error).get().getDescription();

                    case ACK_CHALLENGE:
                        return ChallengeError.valueOf(error).get().getDescription();

                    default:
                        return "Unknown";
                }
            } else {
                return "Invalid packet type: " + packet.getType();
            }
        } catch (NoSuchElementException e) {
            return "Unknown packet error, packet type: " + packet.getType() +
                    ", error:" + packet.getError();
        }
    }

    /**
     * Get error from HUAWEI Packet.
     * @return error string.
     */
    static String buildError(HuaweiPacket packet) {
        Objects.requireNonNull(packet);

        int error = packet.getError();
        Optional<RequestType> type = RequestType.valueOf(packet.getType());
        if (type.isPresent()) {
            try {
                switch (type.get()) {
                    case REQ_CHALLENGE:
                        return ChallengeError.valueOf(error).get().name();

                    case REQ_AUTH:
                        return AuthError.valueOf(error).get().name();

                    case REQ_LOGOUT:
                        return LogoutError.valueOf(error).get().name();
                }
            } catch (NoSuchElementException e) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Failed to parse error, type: {}, error: {}",
                            type.get(), error);
                }
            }
        }

        return "unknown";
    }
}
