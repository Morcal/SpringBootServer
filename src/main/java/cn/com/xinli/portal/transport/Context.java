package cn.com.xinli.portal.transport;

/**
 * Portal transport context.
 *
 * <p>In a portal service process, when clients send requests to remote
 * NAS/BRAS, target devices will respond additional information for
 * further operations, for example, HUAWEI protocol based devices will
 * respond a devices generated unique <code>request id</code> to clients.
 * Those information is saved as context.
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2016/2/23.
 */
public interface Context {
}
