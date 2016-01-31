/**
 * Provides core concepts of portal service.
 *
 * <p>All {@link cn.com.xinli.portal.core.session.Session}s created by
 * {@link cn.com.xinli.portal.core.session.SessionManager} are stored in the
 * {@link cn.com.xinli.portal.core.session.SessionStore}.
 *
 * <p>REST clients need provide {@link cn.com.xinli.portal.core.certificate.Certificate}s in the
 * request credentials, so that server will verify and accept incoming requests.
 *
 * <p>All system activities includes portal service requests, responses, system
 * administrative actions and etc can be logged into {@link cn.com.xinli.portal.core.activity.Activity}s
 * for debugging, tracing, and other purposes.
 */
package cn.com.xinli.portal.core;