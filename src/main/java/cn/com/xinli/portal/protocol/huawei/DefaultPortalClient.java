package cn.com.xinli.portal.protocol.huawei;

import cn.com.xinli.portal.Nas;
import cn.com.xinli.portal.protocol.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;

/**
 * Project: xpws
 *
 * @author zhoupeng 2015/12/22.
 */
public class DefaultPortalClient extends AbstractPortalClient {
    /** Log. */
    private static final Log log = LogFactory.getLog(DefaultPortalClient.class);

    public DefaultPortalClient(Nas nas, CodecFactory codecFactory) {
        super(nas, codecFactory);
    }

    @Override
    protected DatagramPacket createResponseDatagramPacket() {
        try {
            int capacity = 1024; //protocol.getPacketMaxLength();
            String nasIp = StringUtils.isEmpty(nas.getIpv4Address()) ? nas.getIpv6Address() : nas.getIpv4Address();
            return new DatagramPacket(new byte[capacity], capacity, InetAddress.getByName(nasIp), nas.getListenPort());
        } catch (UnknownHostException e) {
            log.error(e);
            throw new PortalProtocolException("Failed to get nas ip", e);
        }
    }

    /**
     * Get ipv4 address in bytes.
     *
     * FIXME: can't apply on ipv6 address.
     * @param ip ipv4 address in form of "xxx.xxx.xxx.xxx".
     * @return ipv4 address in bytes.
     * @throws UnknownHostException
     */
    byte[] getIp4Address(String ip) throws UnknownHostException {
        byte[] bytes = InetAddress.getByName(ip).getAddress();
        if (bytes.length > 4) {
            byte[] dest = new byte[4];
            System.arraycopy(bytes, bytes.length - 4, dest, 0, 4);
            return dest;
        } else {
            return bytes;
        }
    }

    /**
     * Create challenge request packet.
     *
     * @return challenge request packet.
     */
    Packet createChapReqPacket(Credentials credentials) {
        try {
            Packet packet = new Packet();
            packet.setAuthType(AuthType.CHAP.code());
            packet.setIp(getIp4Address(credentials.getIp()));
            if (!StringUtils.isEmpty(credentials.getMac())) {
                packet.addAttribute(Enums.Attribute.USER_MAC, credentials.getMac().getBytes());
            }
            return packet;
        } catch (UnknownHostException e) {
            log.error(e);
            return null;
        }
    }

    /**
     * Create CHAP packet.
     *
     * <p><code>CHAP password = MD5(CHAP id + Password + Challenge)</code></p>
     *
     * @param challenge challenge.
     * @param serialNum serial number.
     * @param reqId challenge request id, from previous challenge response.
     * @param credentials user credentials.
     * @return chap packet.
     */
    Packet createChapAuthPacket(byte[] challenge, int serialNum, int reqId, Credentials credentials) throws IOException {
        Packet packet = new Packet();
        packet.setAuthType(AuthType.CHAP.code());
        packet.setSerialNum(serialNum);
        packet.setReqId(reqId);
        packet.addAttribute(Enums.Attribute.USER_NAME, credentials.getUsername().getBytes());

        if (StringUtils.isEmpty(credentials.getMac())) {
            packet.addAttribute(Enums.Attribute.USER_MAC, credentials.getUsername().getBytes());
        }

        /* Calculate  */
        try (ByteArrayOutputStream bao = new ByteArrayOutputStream()) {
            DataOutputStream output = new DataOutputStream(bao);
            output.writeByte(reqId);
            output.flush();

            bao.write(credentials.getPassword().getBytes());
            bao.write(challenge);
            bao.flush();
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.reset();
            md5.update(bao.toByteArray());
            packet.addAttribute(Enums.Attribute.CHALLENGE_PASSWORD, md5.digest());
        } catch (NoSuchAlgorithmException e) {
            log.error(e);
        }

        return packet;
    }

    /**
     * Create PAP authentication packet.
     *
     * @param credentials user credentials.
     * @return PAP authentication packet.
     */
    Packet createPapAuthPacket(Credentials credentials) {
        Packet packet = new Packet();
        packet.setAuthType(AuthType.PAP.code());
        packet.addAttribute(Enums.Attribute.USER_NAME, credentials.getUsername().getBytes());
        packet.addAttribute(Enums.Attribute.PASSWORD, credentials.getPassword().getBytes());
        if (StringUtils.isEmpty(credentials.getMac())) {
            packet.addAttribute(Enums.Attribute.USER_MAC, credentials.getUsername().getBytes());
        }

        return packet;
    }

    /**
     * Create logout request packet.
     * @param authType authentication type.
     * @param credentials user credentials.
     * @return logout request packet, or null if ip address in credentials is unknown.
     */
    Packet createLogoutPacket(AuthType authType, Credentials credentials) {
        try {
            Packet packet = new Packet();
            packet.setType(Enums.Type.REQ_LOGOUT.code());
            packet.setAuthType(authType.code());
            packet.setIp(getIp4Address(credentials.getIp()));
            packet.setError(Enums.LogoutRequestError.PORTAL_SERVER_REQUEST.code());
            return packet;
        } catch (UnknownHostException e) {
            log.error(e);
            return null;
        }
    }

    /**
     * Create affirmative acknowledge packet.
     *
     * @param serialNum serial number from original response packet.
     * @param reqId request id from original response packet.
     * @param authType authentication type.
     * @return packet.
     */
    private Packet createAckPacket(int serialNum, int reqId, AuthType authType) {
        Packet ack = new Packet();
        ack.setType(Enums.Type.AFF_ACK_AUTH.code());
        ack.setSerialNum(serialNum);
        ack.setReqId(reqId);
        ack.setAuthType(authType.code());
        return ack;
    }

    /**
     * Create not acknowledge (TIMEOUT) packet.
     *
     * <p>NAK packet use {@link Enums.Type#REQ_LOGOUT} as packet type.
     * NAK (TIMEOUT) packet error code must be 1.
     * </p>
     * @param serialNum serial number from original request packet.
     * @param reqId request id from original request packet.
     * @param authType authentication type.
     * @return packet.
     */
    private Packet createNakPacket(int serialNum, int reqId, AuthType authType) {
        Packet nak = new Packet();
        nak.setType(Enums.Type.REQ_LOGOUT.code());
        nak.setSerialNum(serialNum);
        nak.setReqId(reqId);
        nak.setAuthType(authType.code());
        nak.setError(Enums.LogoutRequestError.NAS_RESPONSE_TIMEOUT.code()); // timeout.
        return nak;
    }

    @Override
    public Message login(Credentials credentials) throws IOException {
        AuthType authType = AuthType.of(nas.getAuthType());
        Optional<Packet> response;
        Packet authRequest;

        switch (authType) {
            case CHAP:
                Packet challenge = createChapReqPacket(credentials);
                response = super.request(challenge);
                if (!response.isPresent()) {
                    /* Not respond, send timeout NAK, reqId = 0. */
                    super.nak(createNakPacket(challenge.getSerialNum(), 0, AuthType.CHAP));
                    return Message.failure("NAS not respond to challenge request.");
                }

                Packet chapAck = response.get();
                /* Create portal request to login. */
                authRequest = createChapAuthPacket(
                        chapAck.getAttribute(Enums.Attribute.CHALLENGE),
                        chapAck.getSerialNum(),
                        chapAck.getReqId(),
                        credentials);
                response = super.request(authRequest);
                break;

            case PAP:
                authRequest = createPapAuthPacket(credentials);
                response = super.request(authRequest);
                break;

            default:
                throw new PortalProtocolException("Unsupported authentication type: " + authType);
        }

        /* Check authentication response. */
        if (response.isPresent()) {
            Packet authResponse = response.get();
            if (authResponse.getError() == Enums.AuthError.OK.code()) {
                /* Authentication ok. */
                super.ack(createAckPacket(authRequest.getSerialNum(), authRequest.getReqId(), authType));
            }
            return Message.from(authResponse);
        } else {
            /* Not respond, send nak. */
            super.nak(createNakPacket(authRequest.getSerialNum(), authRequest.getReqId(), authType));
            return Message.failure("NAS not respond to authentication request.");
        }
    }

    @Override
    public Message logout(Credentials credentials) throws IOException {
        AuthType authType = AuthType.of(nas.getAuthType());
        /* Create portal request to logout. */
        Packet logout = createLogoutPacket(authType, credentials);
        if (logout == null) {
            return Message.failure("Failed to create logout request.");
        }

        Optional<Packet> response = super.request(logout);

        if (!response.isPresent()) {
            super.nak(createNakPacket(logout.getSerialNum(), logout.getReqId(), authType));
            return Message.failure("NAS not respond to logout.");
        }

        return Message.from(response.get());
    }
}
