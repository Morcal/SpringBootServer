package cn.com.xinli.portal.web.rest;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

/**
 * Project: rest-api-rest-api
 *
 * @author zhoupeng 2015/12/14.
 */
public class RestResponseParser {
    private static JsonFactory factory = new JsonFactory();

    public static RestResponse parse(String jsonText) throws IOException {

        ObjectMapper mapper = new ObjectMapper(factory);
        try {
            return mapper.readValue(jsonText, RestError.class);
        } catch (JsonMappingException jme) {
            try {
                return mapper.readValue(jsonText, Success.class);
            } catch (JsonMappingException jme2) {
                jme2.printStackTrace();
                return null;
            }
        }
    }
}
