package io.github.taoganio.datasource.provider.test.listener;

import io.github.taoganio.jdbc.provider.spring.boot.JdbcDSReleaseEvent;
import io.github.taoganio.jdbc.provider.spring.boot.JdbcDSSwitchingEvent;
import io.github.taoganio.mongodb.provider.spring.boot.MongoDSReleaseEvent;
import io.github.taoganio.mongodb.provider.spring.boot.MongoDSSwitchingEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class DSEventListener {

    private static final Logger log = LoggerFactory.getLogger(DSEventListener.class);

    @EventListener(JdbcDSSwitchingEvent.class)
    public void jdbcDSSwitchEvent(JdbcDSSwitchingEvent event) {
        log.debug("JDBC DS Switch Event: {}", event.getSource());
    }

    @EventListener(JdbcDSReleaseEvent.class)
    public void jdbcDSReleaseEvent(JdbcDSReleaseEvent event) {
        log.debug("JDBC DS Release Event: {}", event.getSource());
    }

    @EventListener(MongoDSSwitchingEvent.class)
    public void mongoDSSwitchEvent(MongoDSSwitchingEvent event) {
        log.debug("Mongo DS Switch Event: {}", event.getSource());
    }

    @EventListener(MongoDSReleaseEvent.class)
    public void mongoDSReleaseEvent(MongoDSReleaseEvent event) {
        log.debug("Mongo DS Release Event: {}", event.getSource());
    }
}
