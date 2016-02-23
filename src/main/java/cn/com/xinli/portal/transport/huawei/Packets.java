package cn.com.xinli.portal.transport.huawei;

import cn.com.xinli.portal.core.credentials.Credentials;
import cn.com.xinli.portal.transport.AddressUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Objects;

/**
 * HUAWEI portal packet helper.
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2015/12/24.
 */
public final class Packets {
    /** Logger. */
    private static Logger logger = LoggerFactory.getLogger(Packets.class);

    /**
     * Calculate MD5 summary.
     *
     * @param data data to calculate.
     * @return calculated bytes.
     */
    public static byte[] md5sum(byte[] data) {
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
     * @param reqId original request id.
     * @param password password in plain text (ASCII).
     * @param challenge challenge.
     * @return CHAP password in bytes.
     */
    public static byte[] newChapPassword(int reqId, byte[] password, byte[] challenge) {
        Objects.requireNonNull(password, "CHAP password is empty.");
        Objects.requireNonNull(challenge, "CHAP challenge is empty.");
        ByteBuffer buffer = ByteBuffer.allocate(Packet.MAX_LENGTH);
        buffer.put((byte) (reqId & 0xFF));
        buffer.put(password);
        buffer.put(challenge);
        buffer.flip();
        return md5sum(Arrays.copyOfRange(buffer.array(), 0, buffer.remaining()));
    }

    /**
     * Create HUAWEI portal CHAP authentication packet.
     *
     * <p>Packet requires {@link AttributeType#USER_NAME} and {@link AttributeType#CHALLENGE_PASSWORD}.
     * {@link AttributeType#USER_MAC} is optional,
     * {@link AttributeType#USER_IPV6} is optional.
     * {@link AttributeType#CHAP_ID} is optional.
     * {@link AttributeType#TEXT_INFO} is optional.
     *
     * <p><code>CHAP password = MD5(CHAP id + Password + Challenge)</code>
     *
     * @param version protocol version.
     * @param ack challenge acknowledge packet.
     * @param credentials user credentials.
     * @param serialNum serial number.
     * @return HUAWEI portal packet.
     * @throws IOException
     */
    public static Packet newChapAuth(Version version,
                                     Packet ack,
                                     Credentials credentials,
                                     int serialNum) throws IOException {
        Objects.requireNonNull(ack, cn.com.xinli.nio.Packet.EMPTY_PACKET);
        int attrs = 0;
        Packet packet = new Packet();
        packet.setVersion(version.value());
        packet.setType(RequestType.REQ_AUTH.code());
        packet.setAuthType(AuthType.CHAP.code());
        packet.setSerialNum(serialNum);
        packet.setReqId(ack.getReqId());
        packet.setIp(AddressUtils.getIp4Address(credentials.getIp()));
        packet.addAttribute(AttributeType.USER_NAME, credentials.getUsername().getBytes());
        attrs++;
        if (StringUtils.isEmpty(credentials.getMac())) {
            final byte[] mac = AddressUtils.convertMac(credentials.getMac());
            packet.addAttribute(AttributeType.USER_MAC, mac);
            attrs++;
        }

        /* Calculate chap password. */
        packet.addAttribute(AttributeType.CHALLENGE_PASSWORD,
                newChapPassword(
                        ack.getReqId(),
                        credentials.getPassword().getBytes(),
                        ack.getAttribute(AttributeType.CHALLENGE)));
        attrs++;
        packet.setAttrs(attrs);

        return packet;
    }

    /**
     * Create <code>REQ_CHALLENGE</code> packet.
     *
     * @param version protocol version.
     * @param credentials user credentials.
     * @param serialNum serial number.
     * @return challenge request packet.
     * @throws IOException
     */
    public static Packet newChapReq(Version version, Credentials credentials, int serialNum)
            throws IOException {
        int attrs = 0;
        Packet packet = new Packet();
        packet.setVersion(version.value());
        packet.setType(RequestType.REQ_CHALLENGE.code());
        packet.setSerialNum(serialNum);
        packet.setAuthType(AuthType.CHAP.code());
        packet.setIp(AddressUtils.getIp4Address(credentials.getIp()));
        if (!StringUtils.isEmpty(credentials.getMac())) {
            final byte[] mac = AddressUtils.convertMac(credentials.getMac());
            packet.addAttribute(AttributeType.USER_MAC, mac);
            attrs++;
        }
        packet.setAttrs(attrs);
        return packet;
    }

    /**
     * Create PAP authentication packet.
     *
     * <p>Packet requires {@link AttributeType#USER_NAME} and {@link AttributeType#PASSWORD}.
     * {@link AttributeType#USER_MAC} is optional,
     * {@link AttributeType#USER_IPV6} is optional.
     * {@link AttributeType#TEXT_INFO} is optional.
     *
     * @param version protocol version.
     * @param credentials user credentials.
     * @param serialNum serial number.
     * @return PAP authentication packet.
     * @throws IOException
     */
    public static Packet newPapAuth(Version version, Credentials credentials, int serialNum)
            throws IOException {
        int attrs = 0;
        Packet packet = new Packet();
        packet.setVersion(version.value());
        packet.setType(RequestType.REQ_AUTH.code());
        packet.setSerialNum(serialNum);
        packet.setReserved(0);
        packet.setReqId(0);
        packet.setAuthType(AuthType.PAP.code());
        packet.setIp(AddressUtils.getIp4Address(credentials.getIp()));
        packet.addAttribute(AttributeType.USER_NAME, credentials.getUsername().getBytes());
        attrs++;
        packet.addAttribute(AttributeType.PASSWORD, credentials.getPassword().getBytes());
        attrs++;
        if (StringUtils.isEmpty(credentials.getMac())) {
            final byte[] mac = AddressUtils.convertMac(credentials.getMac());
            packet.addAttribute(AttributeType.USER_MAC, mac);
            attrs++;
        }
        packet.setAttrs(attrs);

        return packet;
    }

    /**
     * Create logout request packet.
     *
     * <p>According to HUAWEI portal protocol,
     * When performing a PAP authentication, REQ_LOGOUT request id should
     * be the same as ACK_AUTH response. When performing a CHAP authentication
     * REQ_LOGOUT request id should be the same as ACK_CHALLENGE response.
     * The <code>request id</code> must be already save in session extended
     * information.
     *
     * @param version protocol version.
     * @param authType authentication type.
     * @param credentials user credentials.
     * @param context context.
     * @param serialNum serial number.
     * @return logout request packet,
     * or null if ip address in credentials is unknown.
     * @throws IOException
     */
    public static Packet newLogout(Version version,
                                   AuthType authType,
                                   Credentials credentials,
                                   RequestContext context,
                                   int serialNum) throws IOException {
        Packet packet = new Packet();
        packet.setVersion(version.value());
        packet.setType(RequestType.REQ_LOGOUT.code());
        packet.setAuthType(authType.code());
        packet.setSerialNum(serialNum);
        packet.setReqId(context.getRequestId());
        packet.setIp(AddressUtils.getIp4Address(credentials.getIp()));
        packet.setPort(0);
        packet.setError(LogoutRequestError.REQUEST.code());
        packet.setAttrs(0);
        return packet;
    }

    /**
     * Create <code>AFF_ACK_AUTH</code> packet.
     *
     * @param version protocol version.
     * @param response response packet from remote.
     * @return packet.
     */
    public static Packet newAffAckAuth(Version version, Packet response) {
        Objects.requireNonNull(response, cn.com.xinli.nio.Packet.EMPTY_PACKET);
        Packet ack = new Packet();
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
     * NAK (TIMEOUT) packet error code must be 1. Serial number of a timeout
     * notify packet is same number as originate request. Request id is 0.
     *
     * @param version protocol version.
     * @param request original request.
     * @return request timeout packet.
     */
    public static Packet newTimeout(Version version, Packet request) {
        Objects.requireNonNull(request, cn.com.xinli.nio.Packet.EMPTY_PACKET);
        Packet nak = new Packet();
        nak.setVersion(version.value());
        nak.setType(RequestType.REQ_LOGOUT.code());
        nak.setAuthType(request.getAuthType());
        nak.setReserved(0);
        nak.setSerialNum(request.getSerialNum());
        nak.setReqId(0);
        nak.setIp(request.getIp());
        nak.setError(LogoutRequestError.REQUEST_TIMEOUT.code());
        nak.setAttrs(0);
        return nak;
    }

    /**
     * Create <code>ACK_AUTH</code> packet.
     *
     * <p><code>ACK_AUTH</code> requires BAS_IP in attributes.
     *
     * @param nasAddress nas address.
     * @param reqId request id.
     * @param error authentication error.
     * @param request authentication request.
     * @return response packet.
     */
    public static Packet newAuthAck(InetAddress nasAddress,
                                    int reqId,
                                    AuthError error,
                                    Packet request) {
        Objects.requireNonNull(request, cn.com.xinli.nio.Packet.EMPTY_PACKET);
        Packet response = new Packet();

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
     * Create <code>ACK_CHALLENGE</code> packet.
     *
     * <p><code>ACK_CHALLENGE</code> requires BAS_IP in attributes.
     *
     * @param nasAddress nas address.
     * @param challenge challenge.
     * @param reqId challenge request id.
     * @param error challenge error.
     * @param request challenge request.
     * @return packet.
     */
    public static Packet newChallengeAck(InetAddress nasAddress,
                                         byte[] challenge,
                                         int reqId,
                                         ChallengeError error,
                                         Packet request) {
        Objects.requireNonNull(request, cn.com.xinli.nio.Packet.EMPTY_PACKET);
        Packet response = new Packet();
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
        response.addAttribute(AttributeType.CHALLENGE, challenge);
        response.addAttribute(AttributeType.BAS_IP, nasAddress.getAddress());

        return response;
    }

    /**
     * Create <code>ACK_LOGOUT</code> packet.
     *
     * <p><code>ACK_LOGOUT</code> requires BAS_IP in attributes.
     *
     * @param nasAddress nas address.
     * @param error logout error.
     * @param request logout request.
     * @return packet.
     */
    public static Packet newLogoutAck(InetAddress nasAddress,
                                      LogoutError error,
                                      Packet request) {
        Objects.requireNonNull(request, cn.com.xinli.nio.Packet.EMPTY_PACKET);
        Packet response = new Packet();
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
     * Create <code>NTF_LOGOUT</code> request packet.
     *
     * <p><code>NTF_LOGOUT</code> requires BAS_IP in attributes.
     *
     * @param version protocol version.
     * @param authType authentication type.
     * @param nasAddress NAS address.
     * @param ip user ip address.
     * @param reqId request id.
     * @return packet.
     */
    public static Packet newNtfLogout(Version version,
                                      AuthType authType,
                                      InetAddress nasAddress,
                                      String ip,
                                      int reqId) throws IOException {
        //LogoutRequestError
        Packet ntf = new Packet();
        ntf.setVersion(version.value());
        ntf.setType(RequestType.NTF_LOGOUT.code());
        ntf.setAuthType(authType.code());
        ntf.setSerialNum(0);
        ntf.setReqId(reqId);
        ntf.setIp(AddressUtils.getIp4Address(ip));
        ntf.setPort(0);
        ntf.setReserved(0);
        ntf.setError(LogoutRequestError.REQUEST.code());
        ntf.setAttrs(1);
        ntf.addAttribute(AttributeType.BAS_IP, nasAddress.getAddress());
        return ntf;
    }

    /**
     * Create <code>ACK_NTF_LOGOUT</code> request response ACK packet.
     *
     * <p><code>ACK_NTF_LOGOUT</code> requires BAS_IP in attributes.
     *
     * @param nasAddress NAS address.
     * @param request original request.
     * @param error logout error.
     * @return packet.
     */
    public static Packet newNtfLogoutAck(InetAddress nasAddress,
                                         Packet request,
                                         LogoutError error) {
        Objects.requireNonNull(request, cn.com.xinli.nio.Packet.EMPTY_PACKET);
        //LogoutRequestError
        Packet ntf = new Packet();
        ntf.setVersion(request.getVersion());
        ntf.setType(RequestType.ACK_NTF_LOGOUT.code());
        ntf.setAuthType(request.getAuthType());
        ntf.setSerialNum(request.getSerialNum());
        ntf.setReqId(request.getReqId());
        ntf.setIp(request.getIp());
        ntf.setPort(request.getPort());
        ntf.setReserved(request.getReserved());
        ntf.setError(error.code());
        ntf.setAttrs(1);
        ntf.addAttribute(AttributeType.BAS_IP, nasAddress.getAddress());
        return ntf;
    }

}
