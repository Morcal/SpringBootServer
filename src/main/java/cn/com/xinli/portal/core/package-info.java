/**
 * Provides core concepts of portal service.
 *
 * <p>PWS maintains a {@link cn.com.xinli.portal.core.NasMapping} which keeps
 * tracking which NAS the client came from. When clients request to operation
 * on {@link cn.com.xinli.portal.core.Session}s, server can retrieve the
 * originate NAS/BRAS from that mapping, and then communicate with target NAS/BRAS.
 *
 * <p>All {@link cn.com.xinli.portal.core.Session}s created by
 * {@link cn.com.xinli.portal.core.SessionManager} are stored in the
 * {@link cn.com.xinli.portal.core.SessionStore}.
 *
 * <p>REST clients need provide {@link cn.com.xinli.portal.core.Certificate}s in the
 * request credentials, so that server will verify and accept incoming requests.
 */
package cn.com.xinli.portal.core;