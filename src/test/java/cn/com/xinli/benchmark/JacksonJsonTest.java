package cn.com.xinli.benchmark;

import cn.com.xinli.portal.core.nas.Nas;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
  * <p>Project: xpws
 *
 * @author zhoupeng 2016/2/3.
 */
public class JacksonJsonTest {
    /** Logger. */
    private final Logger logger = LoggerFactory.getLogger(JacksonJsonTest.class);

    static final int COUNT = 100_000;
    static final ObjectMapper mapper = new ObjectMapper();

    final String nasJson = "{" +
            "\"nas_type\":\"HUAWEI\"," +
            "\"id\":1," +
            "\"name\":\"mock-huawei-nas\"," +
            "\"translation\":{" +
            "\"id\":1," +
            "\"modifiers\":[{" +
            "\"id\":1,\"target\":" +
            "\"USERNAME\"," +
            "\"position\":\"TAIL\"," +
            "\"value\":\"@xinli\"" +
            "}]," +
            "\"encoder\":{\"id\":1, \"encoder_type\":\"NO-OP\"}," +
            "\"encoder_value\":null," +
            "\"authenticate_with_domain\":false" +
            "}," +
            "\"version\":\"V2\"," +
            "\"ipv4_address\":\"127.0.0.1\"," +
            "\"ipv6_address\":null," +
            "\"portal_shared_secret\":\"s3cr3t\"," +
            "\"listen_port\":2000," +
            "\"authentication_type\":\"CHAP\"" +
            "}";
    @Test
    public void testJacksonJson() throws IOException {
        // warm up.
        mapper.readValue(nasJson, Nas.class);

        long start = System.currentTimeMillis();
        for (int i = 0; i < COUNT; i++) {
            mapper.readValue(nasJson, Nas.class);
        }
        long timed = System.currentTimeMillis() - start;
        logger.debug("Deserialize json {} times cost {} milliseconds.", COUNT, timed);
    }
}
