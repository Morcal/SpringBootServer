package cn.com.xinli.portal;

import org.springframework.context.ApplicationEvent;

/**
 * Portal web server initialized event.
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2016/2/1.
 */
public class EnvironmentInitializedEvent extends ApplicationEvent {
    public EnvironmentInitializedEvent(Object source) {
        super(source);
    }
}
