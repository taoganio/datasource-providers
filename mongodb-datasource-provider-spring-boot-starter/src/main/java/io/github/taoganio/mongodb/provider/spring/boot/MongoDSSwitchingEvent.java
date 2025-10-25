package io.github.taoganio.mongodb.provider.spring.boot;

import org.springframework.context.ApplicationEvent;

public class MongoDSSwitchingEvent extends ApplicationEvent {

    public MongoDSSwitchingEvent(MongoDSContext source) {
        super(source);
    }

    @Override
    public MongoDSContext getSource() {
        return (MongoDSContext) super.getSource();
    }
}
