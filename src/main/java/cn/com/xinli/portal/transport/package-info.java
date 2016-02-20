/**
 * Provides portal protocol transport.
 *
 * <p>This protocol implementation
 * model is <i>Transport independent</i>, which means that
 * {@link cn.com.xinli.portal.transport.Connector}s need to implement
 * the underlying transportation.
 *
 * <p>{@link cn.com.xinli.portal.transport.huawei.support.HuaweiPortal} is a
 * {@link cn.com.xinli.portal.transport.Connector} factory which employees
 * the "flyweight" design pattern (one of the GoF design patterns) to
 * share common <em>Stateless</em> {@link cn.com.xinli.nio.CodecFactory}(s).
 *
 * <p>Exceptions defined in this package are not extended from other
 * package, so that this package can be run <em>Standalone</em>.
 *
 * @author zhoupeng
 */
package cn.com.xinli.portal.transport;