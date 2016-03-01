package cn.com.xinli.portal.web.auth.token;

import cn.com.xinli.portal.core.Serializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.StringJoiner;

/**
 * Delimiter separated token key serializer.
 *
 * <p>This serializer save token key in a plain text with format of
 * <code>token scope:creation time:random:extended information:SHA summary.</code>
 * if delimiter is ":".
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2016/2/25.
 */
public class DelimitedTokeKeySerializer implements Serializer<TokenKey> {
    /** Logger. */
    private final Logger logger = LoggerFactory.getLogger(DelimitedTokeKeySerializer.class);

    /** Delimiter. */
    private String delimiter;

    public DelimitedTokeKeySerializer(String delimiter) {
        this.delimiter = delimiter;
    }

    @Override
    public byte[] serialize(TokenKey tokenKey) {
        Objects.requireNonNull(tokenKey);

        StringJoiner joiner = new StringJoiner(delimiter);
        joiner.add(tokenKey.getScope().alias())
                .add(String.valueOf(tokenKey.getCreationTime()))
                .add(tokenKey.getRandom())
                .add(tokenKey.getExtendedInformation())
                .add(tokenKey.getDigest());
        return joiner.toString().getBytes();
    }

    @Override
    public TokenKey deserialize(byte[] bytes) {
        if (bytes == null || bytes.length < 1) {
            throw new IllegalArgumentException("invalid token key " + Arrays.toString(bytes));
        }

        String key = new String(bytes);

        String[] tokens = StringUtils.delimitedListToStringArray(key, delimiter);

        if (tokens == null || tokens.length != 5) {
            return null;
        }

        TokenScope scope;
        try {
            scope = TokenScope.of(tokens[0]).get();
        } catch (IllegalArgumentException | NoSuchElementException e) {
            logger.debug("Invalid token scope.");
            return null;
        }

        long creationTime;
        try {
            creationTime = Long.decode(tokens[1]);
        } catch (NumberFormatException e) {
            logger.debug("Invalid token creation time.");
            return null;
        }

        String random = tokens[2];
        String extendedInformation = tokens[3];
        String digest = tokens[4];

        if (StringUtils.isEmpty(extendedInformation)) {
            logger.debug("Empty token extended information.");
            return null;
        }

        TokenKey tokenKey = new TokenKey();
        tokenKey.setScope(scope);
        tokenKey.setCreationTime(creationTime);
        tokenKey.setRandom(random);
        tokenKey.setExtendedInformation(extendedInformation);
        tokenKey.setDigest(digest);

        return tokenKey;
    }
}
