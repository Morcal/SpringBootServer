package cn.com.xinli.portal.support;

import cn.com.xinli.portal.core.PortalError;
import cn.com.xinli.portal.protocol.PortalProtocolException;
import cn.com.xinli.portal.protocol.ProtocolError;

import java.util.Optional;
import java.util.stream.Stream;

/**
 * Portal Error Translator.
 *
 * <p>
 * This class provides functionality to identify {@link PortalProtocolException}s
 * based on known error text signatures,
 * and translate those exceptions to portal error.
 *
 * Project: xpws
 *
 * @author zhoupeng 2016/1/19.
 */
public class PortalErrorTranslator {

    static class Entry {
        String[] identifiers;
        int error;

        static Entry of(int error, String... identifiers) {
            Entry entry = new Entry();
            entry.error = error;
            entry.identifiers = identifiers;
            return entry;
        }

        /** Known Xini-AAA authentication error signatures. */
        static final Entry[] entries = {
            of(231, "1|90|"),
            of(232, "2|91|"),
            of(233, "4|82|"),
            of(234, "8|134|", "8|179|"),
            of(235, "44|-1|"),
            of(236, "-1|-1|"),
            of(237, "8|180|", "8|181|"),
            of(238, "10|53|"),
            of(239, "Login interval is too short"),
            of(240, "You are already logged in - access denied"),
            of(241, "User not found"),
            of(242, "Password Error"),
            of(243, "User state error!Pls.recharge or connect the admin"),
            of(244, "Please the merge account to login"),
            of(245, "user already in"),
        };
    }

    /**
     * Translate portal protocol exception to portal error.
     * @param ex portal protocol exception.
     * @return portal error.
     */
    public static PortalError translate(PortalProtocolException ex) {
        ProtocolError error = ex.getProtocolError();
        switch (error.getText()) {
            case "authentication_failure":
                return translateAuthenticationError(ex.getMessage());
        }

        return PortalError.of("unknown_portal_error");
    }

    /**
     * Check if text contains any one of given string array.
     * @param text text.
     * @param identifies string array.
     * @return true if found match(es).
     */
    private static boolean contains(String text, String[] identifies) {
        for (String id : identifies) {
            if (text.contains(id))
                return true;
        }
        return false;
    }

    /**
     * Translate portal authentication error.
     * @param text error text.
     * @return portal error.
     */
    private static PortalError translateAuthenticationError(String text) {
        Optional<Entry> entry = Stream.of(Entry.entries)
                .filter(e -> contains(text, e.identifiers))
                .findAny();

        if (entry.isPresent()) {
            return PortalError.of(entry.get().error);
        } else {
            return PortalError.of("unknown_login_error");
        }
    }
}
