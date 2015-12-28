/**
 * This package provides portal protocol implementations
 * with support of Huawei Protocol v1 and v2.
 *
 * Both Huawei Protocol {@link cn.com.xinli.portal.protocol.huawei.V1} and
 * {@link cn.com.xinli.portal.protocol.huawei.V2} are implemented as
 * <em>Stateless</em>, and they share the same
 * {@link cn.com.xinli.portal.protocol.huawei.HuaweiCodecFactory} which
 * is also implemented as <em>Stateless</em>. This protocol implementation
 * model is <i>Transport independent</i>, which means
 * {@link cn.com.xinli.portal.protocol.PortalClient}s need to implement
 * the underlying transportation.
 *
 * {@link cn.com.xinli.portal.protocol.support.PortalClients} is a
 * {@link cn.com.xinli.portal.protocol.PortalClient} factory which employees
 * the "flyweight" design pattern (one of the GoF design patterns) to
 * share common <em>Stateless</em> {@link cn.com.xinli.portal.protocol.Protocol}s
 * and {@link cn.com.xinli.portal.protocol.CodecFactory}(s).
 *
 * Exceptions defined in this package are not extended from other
 * package, so that this package can be run <em>Standalone</em>.
 *
 * @author zhoupeng
 */