package cn.com.xinli.portal;

import org.springframework.core.Ordered;

import java.util.Optional;
import java.util.stream.Stream;

/**
 * Portal web server startup stage.
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2016/1/28.
 */
public interface Stage {
    int HIGHEST_ORDER = Ordered.HIGHEST_PRECEDENCE + 100;
    int BOOTSTRAP = HIGHEST_ORDER + 100;
    int CONFIGURE = HIGHEST_ORDER + 200;
    int INITIALIZE = HIGHEST_ORDER + 300;
    int SERVE = HIGHEST_ORDER + 400;
    
    enum Series {
        BOOTSTRAP_STAGE(0, "bootstrap"),
        CONFIGURE_STAGE(1, "configure"),
        INITIALIZE_STAGE(2, "initialize"),
        SERVE_STAGE(3, "serve");

        private final int value;
        private final String name;
        Series(int value, String name) {
            this.value = value;
            this.name = name;
        }

        public int getValue() {
            return value;
        }

        public String getName() {
            return name;
        }
    }

    int getOrder();

    default Series seriesOf(int order) {
        int delta = (order - BOOTSTRAP) / 100;

        Optional<Series> series = Stream.of(Series.values())
                .filter(s -> s.value == delta)
                .findFirst();

        series.orElseThrow(() -> new IllegalArgumentException("Invalid series order: " + order));

        return series.get();
    }
}
