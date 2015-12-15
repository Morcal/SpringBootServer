package cn.com.xinli.portal.util;

import cn.com.xinli.portal.rest.RestResponse;
import cn.com.xinli.portal.rest.bean.Failure;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Project: xpws
 *
 * @author zhoupeng 2015/12/16.
 */
public class ErrorResponseUtil {
    private static final JsonFactory factory = new JsonFactory();

    public static String exceptionError(Exception e) {
        Failure failure = new Failure();
        failure.setError(RestResponse.ERROR_SERVER_ERROR);
        failure.setDescription("Server Internal Error.");

        try {
            return new ObjectMapper(factory).writeValueAsString(failure);
        } catch (JsonProcessingException jpe) {
            return "{\"error\":\"Server Internal Error\"}";
        }
    }
}
