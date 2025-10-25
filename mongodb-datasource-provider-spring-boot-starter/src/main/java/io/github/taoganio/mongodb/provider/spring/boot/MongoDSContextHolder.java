package io.github.taoganio.mongodb.provider.spring.boot;


import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.NamedInheritableThreadLocal;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Mongo数据源上下文持有, 用于切换Mongo数据库连接
 * <p>
 * 当调用{@link MongoDSContextHolder#push(MongoDSContext)}执行完相关操作后
 * 请调用{@link MongoDSContextHolder#poll()}, 清除当前线程持有的数据库连接，以免出现操作数据库冲突
 * <p>
 * 例如：通过{@link MongoDSContextHolder#push(MongoDSContext)}切换到 A连接，
 * 在执行完 A连接 的相关，需要对 B连接 进行操作, 如果在 A连接 之后没有调用{@link MongoDSContextHolder#poll() }
 * 操作 B连接 时, 实际上还是对 A连接 的操作
 */
public abstract class MongoDSContextHolder {

    private static final ThreadLocal<Deque<MongoDSContext>> MONGODB_CONTEXT_THREAD_LOCAL =
            new NamedInheritableThreadLocal<Deque<MongoDSContext>>("mongods-context-holder") {
                @Override
                protected Deque<MongoDSContext> initialValue() {
                    return new ArrayDeque<>();
                }
            };

    @Nullable
    private static ApplicationEventPublisher eventPublisher;

    public static MongoDSContext push(MongoDSContext context) {
        Assert.notNull(context, "MongoDSContext must not be null");
        MONGODB_CONTEXT_THREAD_LOCAL.get().push(context);
        if (eventPublisher != null) {
            eventPublisher.publishEvent(new MongoDSSwitchingEvent(context));
        }
        return context;
    }

    public static MongoDSContext peek() {
        return MONGODB_CONTEXT_THREAD_LOCAL.get().peek();
    }

    public static MongoDSContext poll() {
        Deque<MongoDSContext> contextDeque = MONGODB_CONTEXT_THREAD_LOCAL.get();
        MongoDSContext poll = contextDeque.poll();
        if (contextDeque.isEmpty()) {
            clear();
        }
        if (eventPublisher != null) {
            eventPublisher.publishEvent(new MongoDSReleaseEvent(poll));
        }
        return poll;
    }

    public static void clear() {
        MONGODB_CONTEXT_THREAD_LOCAL.remove();
    }

    public static void setEventPublisher(@Nullable ApplicationEventPublisher eventPublisher) {
        MongoDSContextHolder.eventPublisher = eventPublisher;
    }
}

