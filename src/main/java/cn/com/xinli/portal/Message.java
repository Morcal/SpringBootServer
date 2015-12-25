package cn.com.xinli.portal;

import java.util.Optional;

/**
 * Portal web server message.
 * Project: xpws
 *
 * @author zhoupeng 2015/12/23.
 * @param <T> Message content type.
 */
public interface Message<T> {
    /**
     * Check if this message represents a successful operation.
     * @return true if message represents a successful operation.
     */
    boolean isSuccess();

    /**
     * Get text inside this message.
     * @return text.
     */
    String getText();

    /**
     * Get message content.
     * @return message content.
     */
    Optional<T> getContent();

    /**
     * Create a message contains content and text.
     * @param t message content.
     * @param success if message is success or not.
     * @param text message text.
     * @param <T> message content type.
     * @return message.
     */
    static <T>Message<T> of(T t, boolean success, String text) {
        return new Message<T>() {
            @Override
            public boolean isSuccess() {
                return success;
            }

            @Override
            public String getText() {
                return text;
            }

            @Override
            public Optional<T> getContent() {
                return Optional.ofNullable(t);
            }

            @Override
            public String toString() {
                return "Message{" +
                        "success:" + success +
                        ", text:" + text +
                        ", content: " + t +
                        "}";
            }
        };
    }
}
