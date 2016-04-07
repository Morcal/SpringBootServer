/**
 * Provides HUAWEI portal implementation.
 *
 * <p>PWS includes a embedded portal-server which accepts incoming
 * portal request and respond accordingly. Developers can enable
 * this mimic portal-server as HUAWEI NAS/BRAS by enabling
 * system configuration in "dev" profile.
 *
 * <p>Both HUAWEI Protocol {@link cn.com.xinli.portal.transport.huawei.Version#V1} and
 * {@link cn.com.xinli.portal.transport.huawei.Version#V2} are implemented as
 * <em>Stateless</em>, and they share the same
 * {@link cn.com.xinli.portal.transport.huawei.nio.ByteBufferCodecFactory} which
 * is also implemented as <em>Stateless</em>.
 *
 * <p>{@link cn.com.xinli.portal.transport.huawei.support.HuaweiPortal}
 * is a facade for internal implementations, portal client can create by
 * {@link cn.com.xinli.portal.transport.huawei.support.HuaweiPortal#getConnector(
 * cn.com.xinli.portal.transport.huawei.Endpoint)}.
 * Portal server can be created by
 * {@link cn.com.xinli.portal.transport.huawei.support.HuaweiPortal#createServer(
 * cn.com.xinli.portal.transport.huawei.Endpoint, cn.com.xinli.portal.transport.huawei.ServerHandler)} (
 * Mimic NAS/BRAS can be create by
 * {@link cn.com.xinli.portal.transport.huawei.support.HuaweiPortal#createNas(
 * cn.com.xinli.portal.transport.huawei.Endpoint)}.
 */
package cn.com.xinli.portal.transport.huawei;