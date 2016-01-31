package cn.com.xinli.portal.support.configuration;

import cn.com.xinli.portal.support.PortalErrorTranslator;
import cn.com.xinli.portal.support.PortalErrorTranslator.HttpStatusEntry;
import cn.com.xinli.portal.support.PortalErrorTranslator.MessageEntry;
import cn.com.xinli.portal.support.PortalErrorTranslator.ProtocolEntry;
import cn.com.xinli.portal.support.PortalErrorTranslator.Range;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;

/**
 * PWS error configuration.
 *
 * <p>This class configures
 * <ul>
 *     <li>translate reason error messages to error codes.</li>
 *     <li>translate error codes to HTTP status codes.</li>
 * </ul>
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2016/1/22.
 */
@Configuration
public class ErrorConfiguration {

    /**
     * Protocol to portal error translate table.
     *
     * <p>Protocol errors are thrown by portal-protocol client or server.
     * If server encountered portal-protocol errors, server will translate
     * underlying protocol error to portal error according to this table.
     */
    public static final ProtocolEntry[] PROTOCOL_TRANSLATE_TABLE = {
            ProtocolEntry.of(70, 0xa00),
            ProtocolEntry.of(71, 0xa01),
            ProtocolEntry.of(72, 0xa02),
            ProtocolEntry.of(73, 0xa03),
            ProtocolEntry.of(74, 0xa04),
            ProtocolEntry.of(75, 0xa05),
            ProtocolEntry.of(76, 0xa06),
            ProtocolEntry.of(81, 0xa0b),
            ProtocolEntry.of(82, 0xa0c),
            ProtocolEntry.of(83, 0xa0d),
            ProtocolEntry.of(84, 0xa0e),
            ProtocolEntry.of(85, 0xa0f),
            ProtocolEntry.of(86, 0xa10),
            ProtocolEntry.of(87, 0xa11),
            ProtocolEntry.of(88, 0xa12),
            ProtocolEntry.of(89, 0xa13),
            ProtocolEntry.of(90, 0xa14),
            ProtocolEntry.of(91, 0xa15),
    };

    /**
     * Known Xinli-AAA authentication error signatures.
     * <p>Each entry contains one or more partial content of error message.
     * If error message (from Portal service nodes, such as NAS/BRAS, AAA)
     * contains one of signatures defined in an entry, that error message will
     * translate to the error value defined in the entry.
     */
    public static final MessageEntry[] MESSAGE_TRANSLATE_TABLE = {
            MessageEntry.of(231, "1|90|"),
            MessageEntry.of(232, "2|91|"),
            MessageEntry.of(233, "4|82|"),
            MessageEntry.of(234, "8|134|", "8|179|"),
            MessageEntry.of(235, "44|-1|"),
            MessageEntry.of(236, "-1|-1|"),
            MessageEntry.of(237, "8|180|", "8|181|"),
            MessageEntry.of(238, "10|53|"),
            MessageEntry.of(239, "Login interval is too short"),
            MessageEntry.of(240, "You are already logged in - access denied"),
            MessageEntry.of(241, "User not found"),
            MessageEntry.of(242, "Password Error"),
            MessageEntry.of(243, "User state error!Pls.recharge or connect the admin"),
            MessageEntry.of(244, "Please the merge account to login"),
            MessageEntry.of(245, "user already in"),
    };

    /**
     * Default translation table.
     * <p>Error codes in this table will translate to HTTP status value
     * in the entry.
     */
    public static final HttpStatusEntry[] HTTP_STATUS_TABLE = {
            HttpStatusEntry.of(HttpStatus.BAD_GATEWAY, 12),
            HttpStatusEntry.of(HttpStatus.BAD_REQUEST, 142),
            HttpStatusEntry.of(HttpStatus.FORBIDDEN, Range.of(110, 120), Range.of(152, 299)),
            HttpStatusEntry.of(HttpStatus.GATEWAY_TIMEOUT, 70, 71),
            HttpStatusEntry.of(HttpStatus.GONE, 11),
            HttpStatusEntry.of(HttpStatus.INTERNAL_SERVER_ERROR,
                    Range.of(1, 11), Range.of(13, 13), Range.of(15, 69), Range.of(73, 99)),
            HttpStatusEntry.of(HttpStatus.NOT_FOUND, 122),
            HttpStatusEntry.of(HttpStatus.SERVICE_UNAVAILABLE, 14),
            HttpStatusEntry.of(HttpStatus.TOO_MANY_REQUESTS, 151),
            HttpStatusEntry.of(HttpStatus.UNAUTHORIZED, Range.of(101, 121)),
            HttpStatusEntry.of(HttpStatus.UNPROCESSABLE_ENTITY, 143),
    };

    @Bean
    public PortalErrorTranslator errorTranslator() {
        PortalErrorTranslator translator = new PortalErrorTranslator();

        translator.setMessageTable(MESSAGE_TRANSLATE_TABLE);
        translator.setHttpStatusTable(HTTP_STATUS_TABLE);
        translator.setProtocolTable(PROTOCOL_TRANSLATE_TABLE);

        return translator;
    }
}
