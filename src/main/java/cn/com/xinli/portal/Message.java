package cn.com.xinli.portal;

/**
 * Project: xpws
 *
 * @author zhoupeng 2015/12/23.
 */
public interface Message<T> {
    boolean isSuccess();

    String getText();

    T getContent();

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
            public T getContent() {
                return t;
            }

            @Override
            public String toString() {
                return "Message{" +
                        "success:" + success +
                        ", text:" + text +
                        ", target: " + t +
                        "}";
            }
        };
    }
}
