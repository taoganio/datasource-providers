package io.github.taoganio.jdbc.provider.spring.boot;

import org.springframework.context.ApplicationEvent;

/**
 * JDBC数据源切换事件
 */
public class JdbcDSSwitchingEvent extends ApplicationEvent {

    private final JdbcDSContext context;

    public JdbcDSSwitchingEvent(JdbcDSContext context) {
        super(context);
        this.context = context;
    }

    public JdbcDSContext getContext() {
        return context;
    }
}
