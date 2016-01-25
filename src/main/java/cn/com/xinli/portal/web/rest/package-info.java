/**
 * This package provides support for Portal REST service.
 *
 * <p>PWS server provides REST service for client to use portal service.
 * PWS provides a {@link cn.com.xinli.portal.web.rest.Scheme} for client to found
 * all {@link cn.com.xinli.portal.web.rest.EntryPoint}s on the server.
 *
 * <p>PWS REST APIs need clients to provide {@link cn.com.xinli.portal.web.rest.Authentication}
 * and {@link cn.com.xinli.portal.web.rest.Authorization} before any REST operations.
 *
 * <p>Portal connections are encoded with JSON format of {@link cn.com.xinli.portal.web.rest.SessionBean}.
 * And PWS respond all portal connection related operations with
 * {@link cn.com.xinli.portal.web.rest.SessionResponse}s.
 *
 * <p>Any REST APIs related error will be represented with {@link cn.com.xinli.portal.web.rest.RestError}.
 */
package cn.com.xinli.portal.web.rest;