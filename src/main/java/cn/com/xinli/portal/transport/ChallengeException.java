package cn.com.xinli.portal.transport;

/**
 * CHAP challenge exception.
 *
 * <p>This exception will throw when portal clients can not process
 * CHAP challenge properly.
 *
 * <p>For clients, it will throw when received CHAP challenge from remote server,
 * and it contains CHAP challenge error code in it.
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2016/1/18.
 */
public class ChallengeException extends PortalProtocolException {
    public ChallengeException(ProtocolError error, String message) {
        super(error, message);
    }
}
