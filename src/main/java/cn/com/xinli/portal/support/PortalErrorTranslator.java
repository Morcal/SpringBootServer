package cn.com.xinli.portal.support;

import cn.com.xinli.portal.core.PortalError;
import cn.com.xinli.portal.core.ServerException;
import cn.com.xinli.portal.transport.TransportException;
import cn.com.xinli.portal.transport.TransportError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

import java.util.*;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Portal Error Translator.
 *
 * <ul>
 *     <li>translate text error messages to error codes.</li>
 *      Provides functionality to identify {@link TransportException}s
 *      based on known error text signatures,
 *      and translate those exceptions to portal error.
 *     <li>translate error codes to HTTP status codes.</li>
 *      This class also provides functionality to translate {@link PortalError}s to
 *      HTTP status codes.
 * </ul>
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2016/1/19.
 */
public class PortalErrorTranslator {
    /** Logger. */
    private final Logger logger = LoggerFactory.getLogger(PortalErrorTranslator.class);

    /** Message translate table. */
    private MessageEntry[] messageTable;

    /** HTTP status translate table. */
    private HttpStatusEntry[] httpStatusTable;

    /** Protocol error translate table. */
    private ProtocolEntry[] protocolTable;

    /**
     * Internal portal error codes to HTTP status codes mapping.
     * key: portal error code,
     * value: HTTP status code.
     */
    static final Map<Integer, Integer> statusCodeMapping = Collections.synchronizedMap(new HashMap<>());

    public void setMessageTable(MessageEntry[] table) {
        this.messageTable = table;
    }

    public void setHttpStatusTable(HttpStatusEntry[] httpStatusTable) {
        this.httpStatusTable = httpStatusTable;
    }

    public void setProtocolTable(ProtocolEntry[] protocolTable) {
        this.protocolTable = protocolTable;
    }

    /**
     * Translate portal error to HTTP status code.
     *
     * <p>This method implements a lazy-load mapping for translation.
     * If no HTTP status defined for a portal error, it will return
     * a {@link HttpStatus#INTERNAL_SERVER_ERROR} as default.
     *
     * @param error portal error.
     * @return http status code.
     */
    public int translate(PortalError error) {
        Objects.requireNonNull(error, PortalError.EMPTY_ERROR);
        int err = error.getValue();
        synchronized (statusCodeMapping) {
            if (!statusCodeMapping.containsKey(err)) {
                Optional<HttpStatusEntry> entry = Stream.of(httpStatusTable)
                        .filter(e -> e.contains(err))
                        .findAny();

                if (entry.isPresent()) {
                    statusCodeMapping.put(err, entry.get().status);
                } else {
                    logger.error("no HTTP status defined for: {}", error);
                    statusCodeMapping.put(err, HttpStatus.INTERNAL_SERVER_ERROR.value());
                }

            }
            return statusCodeMapping.get(err);
        }
    }

    /**
     * Translate {@link TransportError}s to {@link PortalError}s.
     *
     * <p>If Protocol error is an authentication error, server should
     * look up the authentication message table to translate error to
     * a vendor specified error.
     *
     * @param ex portal protocol exception.
     * @return portal error.
     * @throws ServerException If no portal error defined for protocol error
     * or no portal error defined for protocol error.
     */
    public PortalError translate(TransportException ex) throws ServerException {
        TransportError error = ex.getProtocolError();

        if (error.isAuthenticationError()) {
            // Translate portal authentication error.
            Optional<MessageEntry> entry = Stream.of(messageTable)
                    .filter(e -> contains(ex.getMessage(), e.identifiers))
                    .findAny();
            if (entry.isPresent()) {
                return PortalError.of(entry.get().error);
            }
        }

        // fall back to translate table.
        Optional<ProtocolEntry> entry = Stream.of(protocolTable)
                .filter(e -> e.protocolError == error.getValue())
                .findAny();

        entry.orElseThrow(() -> new ServerException(
                PortalError.UNKNOWN_TRANSPORT_ERROR, String.valueOf(error)));

        return PortalError.of(entry.get().portalError);
    }
//
//    /**
//     * Translate {@link TransportError}s to {@link PortalError}s.
//     *
//     * @param error protocol error.
//     * @return portal error.
//     * @throws ServerException If no portal error defined for protocol error.
//     */
//    private PortalError translate(TransportError error) throws ServerException {
//        Optional<ProtocolEntry> entry = Stream.of(protocolTable)
//                .filter(e -> e.protocolError == error.getValue())
//                .findAny();
//
//        entry.orElseThrow(() -> new ServerException(
//                PortalError.UNKNOWN_TRANSPORT_ERROR, String.valueOf(error)));
//
//        return PortalError.of(entry.get().portalError);
//    }

    /**
     * Check if text contains any one of given string array.
     *
     * @param text       text.
     * @param identifies string array.
     * @return true if found match(es).
     */
    private boolean contains(String text, String[] identifies) {
        for (String id : identifies) {
            if (text.contains(id))
                return true;
        }
        return false;
    }

//    /**
//     * Translate portal authentication error.
//     *
//     * @param text error text.
//     * @return portal error.
//     * @throws ServerException If no portal error defined for portal message text.
//     */
//    private PortalError translateAuthenticationError(String text) throws ServerException {
//        Optional<MessageEntry> entry = Stream.of(messageTable)
//                .filter(e -> contains(text, e.identifiers))
//                .findAny();
//
//        entry.orElseThrow(() ->
//                new ServerException(PortalError.UNKNOWN_TRANSPORT_ERROR, text));
//
//        return PortalError.of(entry.get().error);
//    }

    /**
     * Translator internal entry.
     * <p>This class uses partial content of message as identifier.
     * <p>Each entry contains one or more partial content of error message.
     * If error message (from Portal service nodes, such as NAS/BRAS, AAA)
     * contains one of signatures defined in an entry, that error message will
     * translate to the error code defined in the entry.
     */
    public static class MessageEntry {
        /** Identifiers. */
        private final String[] identifiers;

        /** Portal service error code which message will be translated to. */
        private final int error;

        public MessageEntry(int error, String[] identifiers) {
            this.error = error;
            this.identifiers = identifiers;
        }

        public static MessageEntry of(int error, String... identifiers) {
            return new MessageEntry(error, identifiers);
        }
    }

    /** Simple error code range. */
    public static class Range {
        /** Range start value (inclusive). */
        final int start;

        /** Range end value (inclusive). */
        final int end;

        private Range(int start, int end) {
            this.start = start;
            this.end = end;
        }

        /**
         * Create an error code range from start to end (inclusive).
         * @param start start error code.
         * @param end end error code.
         * @return error code range.
         */
        public static Range of(int start, int end) {
            return new Range(start, end);
        }
    }

    /**
     * HTTP status codes translator internal entry.
     * <p>When exceptions/errors occurred when process clients' requests,
     * PWS respond an error message with specific HTTP status code.
     * This class defines Entry of the translation table.
     */
    public static class HttpStatusEntry {

        /** Included error codes. */
        final int[] errors;

        /** Included error codes range. */
        final Range[] ranges;

        /** Result http status code. */
        final int status;

        private HttpStatusEntry(int status, int[] errors, Range[] ranges) {
            this.errors = errors;
            this.ranges = ranges;
            this.status = status;
        }

        /**
         * Create a HTTP status error entry with errors in an array.
         * @param status HTTP status translate to.
         * @param errors error codes in an array.
         * @return HTTP status entry.
         */
        public static HttpStatusEntry of(HttpStatus status, int... errors) {
            return new HttpStatusEntry(status.value(), errors, null);
        }

        /**
         * Create a HTTP status error entry with error code ranges in an array.
         * @param status HTTP status translate to.
         * @param ranges error code ranges in an array.
         * @return HTTP status entry.
         */
        public static HttpStatusEntry of(HttpStatus status, Range... ranges) {
            return new HttpStatusEntry(status.value(), null, ranges);
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
    }

    /** Protocol error translate table entry. */
    public static class ProtocolEntry {
        /** Protocol error code. */
        private final int protocolError;

        /** Portal error code translate to. */
        private final int portalError;

        private ProtocolEntry(int portalError, int protocolError) {
            this.portalError = portalError;
            this.protocolError = protocolError;
        }

        public static ProtocolEntry of(int portalError, int protocolError) {
            return new ProtocolEntry(portalError, protocolError);
        }
    }
}
