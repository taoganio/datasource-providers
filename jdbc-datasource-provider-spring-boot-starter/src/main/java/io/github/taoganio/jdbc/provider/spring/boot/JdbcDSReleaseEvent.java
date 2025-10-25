package io.github.taoganio.jdbc.provider.spring.boot;

import org.springframework.context.ApplicationEvent;

/**
 * JDBC数据源释放事件
 */
public class JdbcDSReleaseEvent extends ApplicationEvent {

    public JdbcDSReleaseEvent(JdbcDSContext context) {
        super(context);
    }

    @Override
    public JdbcDSContext getSource() {
        return (JdbcDSContext) super.getSource();
    }
}
