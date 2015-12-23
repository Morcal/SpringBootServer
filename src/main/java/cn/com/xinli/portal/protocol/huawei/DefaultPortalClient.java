package cn.com.xinli.portal.protocol.huawei;

import cn.com.xinli.portal.Message;
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
import java.util.Arrays;
import java.util.Optional;

/**
 * Default portal client.
 * <p>
 * Project: xpws
 *
 * @author zhoupeng 2015/12/22.
 */
public class DefaultPortalClient extends AbstractPortalClient {
    /** Log. */
    private static final Log log = LogFactory.getLog(DefaultPortalClient.class);


    public DefaultPortalClient(int version, Nas nas, CodecFactory codecFactory) {
        super(version, nas, codecFactory);
    }

    @Override
    protected DatagramPacket createResponseDatagramPacket() {
        int capacity = 1024;
        byte[] buf = new byte[capacity];
        return new DatagramPacket(buf, buf.length);
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
    byte[] getIp4Address(String ip) throws UnknownHostException {
        byte[] bytes = InetAddress.getByName(ip).getAddress();
        if (bytes.length > 4) {
            return Arrays.copyOfRange(bytes, bytes.length - 4, bytes.length);
        } else {
            return Arrays.copyOf(bytes, 4);
        }
    }

    /**
     * Create challenge request packet.
     *
     * @return challenge request packet.
     */
    Packet createChapReqPacket(Credentials credentials) {
        try {
            int attrs = 0;
            Packet packet = new Packet();
            packet.setVersion(version);
            packet.setType(Enums.Type.REQ_CHALLENGE.code());
            packet.setSerialNum(Packet.nextSerialNum());
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
     * Create CHAP packet.
     * <p>
     * <code>CHAP password = MD5(CHAP id + Password + Challenge)</code></p>
     *
     * @param challenge   challenge.
     * @param serialNum   serial number.
     * @param reqId       challenge request id, from previous challenge response.
     * @param credentials user credentials.
     * @return chap packet.
     */
    Packet createChapAuthPacket(byte[] challenge,
                                int serialNum,
                                int reqId,
                                Credentials credentials) throws IOException {
        int attrs = 0;
        Packet packet = new Packet();
        packet.setVersion(version);
        packet.setType(Enums.Type.REQ_AUTH.code());
        packet.setAuthType(AuthType.CHAP.code());
        packet.setSerialNum(serialNum);
        packet.setReqId(reqId);
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
            output.writeByte(reqId);
            output.flush();

            bao.write(credentials.getPassword().getBytes());
            bao.write(challenge);
            bao.flush();
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.reset();
            md5.update(bao.toByteArray());
            packet.addAttribute(Enums.Attribute.CHALLENGE_PASSWORD, md5.digest());
            attrs++;
            packet.setAttrs(attrs);
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
    Packet createPapAuthPacket(Credentials credentials) throws IOException {
        int attrs = 0;
        Packet packet = new Packet();
        packet.setVersion(version);
        packet.setType(Enums.Type.REQ_AUTH.code());
        packet.setSerialNum(Packet.nextSerialNum());
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
     * @param authType    authentication type.
     * @param credentials user credentials.
     * @return logout request packet, or null if ip address in credentials is unknown.
     */
    Packet createLogoutPacket(AuthType authType, Credentials credentials) throws IOException {
        Packet packet = new Packet();
        packet.setVersion(version);
        packet.setSerialNum(Packet.nextSerialNum());
        packet.setType(Enums.Type.REQ_LOGOUT.code());
        packet.setAuthType(authType.code());
        packet.setIp(getIp4Address(credentials.getIp()));
        packet.setError(Enums.LogoutRequestError.PORTAL_SERVER_REQUEST.code());
        return packet;
    }

    /**
     * Create affirmative acknowledge packet.
     *
     * @param serialNum serial number from original response packet.
     * @param reqId     request id from original response packet.
     * @param ip        request ip address.
     * @param authType  authentication type.
     * @return packet.
     */
    private Packet createAckPacket(int serialNum, int reqId, byte[] ip, AuthType authType) {
        Packet ack = new Packet();
        ack.setVersion(version);
        ack.setType(Enums.Type.AFF_ACK_AUTH.code());
        ack.setSerialNum(serialNum);
        ack.setReqId(reqId);
        ack.setIp(ip);
        ack.setAuthType(authType.code());
        return ack;
    }

    /**
     * Create not acknowledge (TIMEOUT) packet.
     * <p>
     * NAK packet use {@link Enums.Type#REQ_LOGOUT} as packet type.
     * NAK (TIMEOUT) packet error code must be 1.
     * </p>
     *
     * @param serialNum serial number from original request packet.
     * @param reqId     request id from original request packet.
     * @param ip        request ip address.
     * @param authType  authentication type.
     * @return packet.
     */
    private Packet createNakPacket(int serialNum, int reqId, byte[] ip, AuthType authType) {
        Packet nak = new Packet();
        nak.setVersion(version);
        nak.setType(Enums.Type.REQ_LOGOUT.code());
        nak.setSerialNum(serialNum);
        nak.setReqId(reqId);
        nak.setAuthType(authType.code());
        nak.setIp(ip);
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
                    super.nak(createNakPacket(challenge.getSerialNum(), 0, challenge.getIp(), AuthType.CHAP));
                    return PortalMessage.failure("NAS not respond to challenge request.");
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
                log.debug("> auth ok, send affirmative acknowledge.");
                /* Authentication ok, send affirmative acknowledge. */
                super.ack(
                        createAckPacket(
                                authRequest.getSerialNum(),
                                authRequest.getReqId(),
                                authRequest.getIp(),
                                authType));
            }
            return PortalMessage.from(authResponse);
        } else {
            log.debug("+ auth not response, send nak.");
            /* Not respond, send nak. */
            super.request(
                    createNakPacket(
                            authRequest.getSerialNum(),
                            authRequest.getReqId(),
                            authRequest.getIp(),
                            authType));
            return PortalMessage.failure("NAS not respond to authentication request.");
        }
    }

    @Override
    public Message logout(Credentials credentials) throws IOException {
        AuthType authType = AuthType.of(nas.getAuthType());
        /* Create portal request to logout. */
        Packet logout = createLogoutPacket(authType, credentials);
        if (logout == null) {
            return PortalMessage.failure("Failed to create logout request.");
        }

        Optional<Packet> response = super.request(logout);

        if (!response.isPresent()) {
            super.nak(createNakPacket(logout.getSerialNum(), logout.getReqId(), logout.getIp(), authType));
            return PortalMessage.failure("NAS not respond to logout.");
        }

        return PortalMessage.from(response.get());
    }
}
