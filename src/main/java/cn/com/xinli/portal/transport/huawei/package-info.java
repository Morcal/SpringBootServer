/**
 * Provides Huawei portal implementation.
 *
 * <p>PWS includes a embedded portal-server which accepts incoming
 * portal request and respond accordingly. Developers can enable
 * this mimic portal-server as Huawei NAS/BRAS by enabling
 * system configuration in pws.properties.
 *
 * <p>{@link cn.com.xinli.portal.protocol.huawei.HuaweiPortal}
 * is a facade for internal implementations, portal client can create by
 * {@link cn.com.xinli.portal.protocol.huawei.HuaweiPortal#createClient(
 * cn.com.xinli.portal.protocol.Nas)}.
 * Portal server can be created by {@link cn.com.xinli.portal.protocol.huawei.HuaweiPortal#createServer(
 * cn.com.xinli.portal.protocol.PortalServerConfig, PortalServerHandler)}.
 * Mimic NAS/BRAS can be create by {@link cn.com.xinli.portal.protocol.huawei.HuaweiPortal#createNas(
 * cn.com.xinli.portal.protocol.Nas)}
 *
 * <p>A default Huawei portal client was implemented by
 * {@link cn.com.xinli.portal.protocol.huawei.DefaultPortalClient}, callers
 * can create client by {@link cn.com.xinli.portal.protocol.huawei.HuaweiPortal#createClient(
 * cn.com.xinli.portal.protocol.Nas)}.
 *
 */
package cn.com.xinli.portal.transport.huawei;