/**
 * PProvides <a href="http://redis.io">REDIS</a> based implementation
 * classes for "cluster" mode.
 *
 * <p>In cluster (distributed) environment, each class implements
 * {@link cn.com.xinli.portal.core.DataStore} can not use local-generated
 * identifiers, instead a shared data store managed identifier should
 * be used. So it's prorate to use REDIS to generate identifiers just like
 * use database to generate identifiers.
 */
package cn.com.xinli.portal.support.redis;