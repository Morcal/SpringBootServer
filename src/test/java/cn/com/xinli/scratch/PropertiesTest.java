package cn.com.xinli.scratch;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;
import java.util.Set;

/**
 * @author zhoupeng, created on 2016/3/31.
 */
public class PropertiesTest {
    private final Logger logger = LoggerFactory.getLogger(PropertiesTest.class);

    @Test
    public void testPropertiesWithDefaults() {

        Properties defList = new Properties();
        defList.put("Florida", "Tallahassee");
        defList.put("Wisconsin", "Madison");

        Properties capitals = new Properties(defList);

        capitals.put("Illinois", "Springfield");
        capitals.put("Missouri", "Jefferson City");
        capitals.put("Washington", "Olympia");
        capitals.put("California", "Sacramento");
        capitals.put("Indiana", "Indianapolis");

        Set states = capitals.keySet();

        for (Object name : states)
            logger.debug("{} / {}", name, capitals.getProperty((String) name));

        String str = capitals.getProperty("Florida");
        logger.debug("The capital of Florida is {}", str);
    }
}
