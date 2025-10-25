package io.github.taoganio.jdbc.provider.spring.boot;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.NamedInheritableThreadLocal;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * JDBC数据源上下文持有, 用于切换JDBC数据库连接
 * <p>
 * 当调用{@link JdbcDSContextHolder#push(JdbcDSContext)}执行完相关操作后
 * 请调用{@link JdbcDSContextHolder#poll()}, 清除当前线程持有的数据库连接，以免出现操作数据库冲突
 * <p>
 * 例如：通过{@link JdbcDSContextHolder#push(JdbcDSContext)}切换到 A连接，
 * 在执行完 A连接 的相关，需要对 B连接 进行操作, 如果在 A连接 之后没有调用{@link JdbcDSContextHolder#poll() }
 * 操作 B连接 时, 实际上还是对 A连接 的操作
 */
public abstract class JdbcDSContextHolder {

    private static final ThreadLocal<Deque<JdbcDSContext>> JDBC_CONTEXT_THREAD_LOCAL =
            new NamedInheritableThreadLocal<Deque<JdbcDSContext>>("jdbcds-context-holder") {
                @Override
                protected Deque<JdbcDSContext> initialValue() {
                    return new ArrayDeque<>();
                }
            };

    @Nullable
    private static ApplicationEventPublisher eventPublisher;

    public static JdbcDSContext push(JdbcDSContext context) {
        Assert.notNull(context, "JdbcDSContext must not be null");
        JDBC_CONTEXT_THREAD_LOCAL.get().push(context);
        if (eventPublisher != null) {
            eventPublisher.publishEvent(new JdbcDSSwitchingEvent(context));
        }
        return context;
    }

    public static JdbcDSContext peek() {
        return JDBC_CONTEXT_THREAD_LOCAL.get().peek();
    }

    public static JdbcDSContext poll() {
        Deque<JdbcDSContext> contextDeque = JDBC_CONTEXT_THREAD_LOCAL.get();
        JdbcDSContext poll = contextDeque.poll();
        if (contextDeque.isEmpty()) {
            clear();
        }
        if (eventPublisher != null) {
            eventPublisher.publishEvent(new JdbcDSReleaseEvent(poll));
        }
        return poll;
    }

    public static void clear() {
        JDBC_CONTEXT_THREAD_LOCAL.remove();
    }

    public static void setEventPublisher(@Nullable ApplicationEventPublisher eventPublisher) {
        JdbcDSContextHolder.eventPublisher = eventPublisher;
    }
}
