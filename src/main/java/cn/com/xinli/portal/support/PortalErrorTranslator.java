package cn.com.xinli.portal.support;

import cn.com.xinli.portal.core.PortalError;
import cn.com.xinli.portal.protocol.PortalProtocolException;
import cn.com.xinli.portal.protocol.ProtocolError;
import org.springframework.http.HttpStatus;

import java.util.*;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Portal Error Translator.
 * <p>
 * This class provides functionality to identify {@link PortalProtocolException}s
 * based on known error text signatures,
 * and translate those exceptions to portal error.
 * <p>
 * This class also provides functionality to translate {@link PortalError}s to
 * HTTP status codes.
 * <p>
 * Project: xpws
 *
 * @author zhoupeng 2016/1/19.
 */
public class PortalErrorTranslator {
    /** Translator internal entry. */
    static class Entry {
        String[] identifiers;
        int error;

        static Entry of(int error, String... identifiers) {
            Entry entry = new Entry();
            entry.error = error;
            entry.identifiers = identifiers;
            return entry;
        }

        /**
         * Known Xinli-AAA authentication error signatures.
         */
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

    /** Internal portal error codes to HTTP status codes mapping. */
    static final Map<Integer, Integer> statusCodeMapping = Collections.synchronizedMap(new HashMap<>());

    /** HTTP status codes translator internal entry. */
    static class HttpTranslateEntry {
        /** Simple error code range. */
        static class Range {
            final int start;
            final int end;

            private Range(int start, int end) {
                this.start = start;
                this.end = end;
            }

            static Range of(int start, int end) {
                return new Range(start, end);
            }
        }

        /** Included error codes. */
        int[] errors;

        /** Included error codes range. */
        Range[] ranges;

        /** Result http status code. */
        int status;

        private HttpTranslateEntry(int status, int[] errors, Range[] ranges) {
            this.errors = errors;
            this.ranges = ranges;
            this.status = status;
        }

        static HttpTranslateEntry of(HttpStatus status, int... errors) {
            return new HttpTranslateEntry(status.value(), errors, null);
        }

        static HttpTranslateEntry of(HttpStatus status, Range... ranges) {
            return new HttpTranslateEntry(status.value(), null, ranges);
        }

        /**
         * Check if this entry contains given portal error code.
         * @param error portal error code.
         * @return true if this entry contains error code.
         */
        boolean contains(int error) {
            return ((errors != null && IntStream.of(errors).filter(i -> i == error).findAny().isPresent()) ||
                    (ranges != null && Stream.of(ranges)
                            .filter(r -> r.start <= error && r.end >= error)
                            .findAny().isPresent()));
        }

        /** Default translation table. */
        static final HttpTranslateEntry[] entries = {
                of(HttpStatus.BAD_GATEWAY, 12),
                of(HttpStatus.BAD_REQUEST, 141),
                of(HttpStatus.FORBIDDEN, Range.of(110, 120), Range.of(152, 299)),
                of(HttpStatus.GATEWAY_TIMEOUT, 70, 71),
                of(HttpStatus.GONE, 11),
                of(HttpStatus.INTERNAL_SERVER_ERROR, Range.of(1, 11), Range.of(13, 13), Range.of(15, 69), Range.of(73, 99)),
                of(HttpStatus.NOT_FOUND, 122),
                of(HttpStatus.SERVICE_UNAVAILABLE, 14),
                of(HttpStatus.TOO_MANY_REQUESTS, 151),
                of(HttpStatus.UNAUTHORIZED, Range.of(101, 109)),
                of(HttpStatus.UNPROCESSABLE_ENTITY, 142),
        };
    }

    /**
     * Translate portal error to HTTP status code.
     * @param error portal error.
     * @return http status code.
     */
    public static int translate(PortalError error) {
        Objects.requireNonNull(error);
        int err = error.getCode();
        synchronized (statusCodeMapping) {
            if (!statusCodeMapping.containsKey(err)) {
                HttpTranslateEntry entry = Stream.of(HttpTranslateEntry.entries)
                        .filter(e -> e.contains(err))
                        .findAny()
                        .get();
                statusCodeMapping.put(err, entry.status);
            }
            return statusCodeMapping.get(err);
        }
    }

    /**
     * Translate portal protocol exception to portal error.
     *
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
     *
     * @param text       text.
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
     *
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
