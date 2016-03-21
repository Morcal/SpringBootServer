package cn.com.xinli.portal.web.auth;

import cn.com.xinli.portal.web.auth.token.DigestCredentials;
import cn.com.xinli.portal.web.util.CodecUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * HTTP Authentication credential.
 *
 * <p>PWS REST APIs authentication is built on HTTP headers.
 * Which means that incoming REST API requests must provide authentiaction
 * credentials inside http headers.
 *
 * <p>The authentication credentials requires certain encoding and
 * additional signature which should be calculated against credentials itself,
 * HTTP request entities and secret key shared by PWS and client vendors.
 *
 * <p>Project: xpws.
 *
 * @author zhoupeng 2015/12/9.
 */
public class HttpDigestCredentials extends DigestCredentials {
    /** Log. */
    private static final Logger logger = LoggerFactory.getLogger(HttpDigestCredentials.class);

    /** HTTP Header name. */
    public static final String HEADER_NAME = "X-Xinli-Auth";

    /** HTTP authentication scheme name. */
    public static final String SCHEME = "Digest";

    /** HTTP header credentials pair delimiter. */
    public static final String CREDENTIAL_PAIR_DELIMITER = "=";

    /** HTTP header credentials expression delimiter. */
    public static final String CREDENTIAL_DELIMITER_REGEX = ", ";

    /** HTTP header credentials expression. */
    private static final String CREDENTIAL_REGEXP = "^([a-zA-Z0-9_]+)=\"(.*)\"$";

    /**
     * Create a http digest authentication from HTTP HEADER Authentication.
     * @param httpAuthentication HTTP Header Authentication credentials.
     * @return http digest authentication.
     * @throws BadRestCredentialsException if authentication not well-formed.
     */
    public static HttpDigestCredentials of(String httpAuthentication) throws BadRestCredentialsException {
        if (StringUtils.isEmpty(httpAuthentication)) {
            throw new IllegalArgumentException("HTTP Digest authentication credentials can not be empty.");
        }

        if (!httpAuthentication.startsWith(SCHEME + " ")) {
            throw new BadRestCredentialsException("Invalid authentication scheme.");
        }

        HttpDigestCredentials credentials = new HttpDigestCredentials();

        try {
            String content = httpAuthentication.substring(httpAuthentication.indexOf(SCHEME) + SCHEME.length() + 1);
            /* In case of credentials ends with CREDENTIAL_DELIMITER_REGEX and
             * it will work even if it's wrong, so we trim the content
             * So that kind of credentials will be recognized as invalid.
             */
            content = content.trim();
            for (String pair : content.split(CREDENTIAL_DELIMITER_REGEX)) {
                Matcher matcher = Pattern.compile(CREDENTIAL_REGEXP).matcher(pair.trim());
                if (!matcher.matches() || matcher.groupCount() < 2) {
                    throw new BadRestCredentialsException();
                }

                String key = matcher.group(1), value = matcher.group(2);
                value = CodecUtils.urlDecode(value);
                credentials.setAttribute(key, value);
            }
        } catch (UnsupportedEncodingException e) {
            throw new BadRestCredentialsException("Unsupported encoding in credentials.", e);
        }

        return credentials;
    }

    /**
     * Get credentials without signature.
     * @return credentials in string form without signature.
     */
    public String asString(boolean includeSignature) {
        return SCHEME + " " + getAttributes().stream()
                .filter(e -> !includeSignature && !e.getKey().equals(SIGNATURE))
                .map(entry -> {
                    try {
                        return CodecUtils.urlEncode(entry.getKey()) + CREDENTIAL_PAIR_DELIMITER + '"' +
                                CodecUtils.urlEncode(entry.getValue()) + '"';
                    } catch (UnsupportedEncodingException e) {
                        logger.error("Failed to encode credential: {}", toString());
                        return "";
                    }
                })
                .collect(Collectors.joining(CREDENTIAL_DELIMITER_REGEX));
    }
}
