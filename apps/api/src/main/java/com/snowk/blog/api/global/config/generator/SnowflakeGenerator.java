package com.snowk.blog.api.global.config.generator;

import java.util.EnumSet;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.generator.BeforeExecutionGenerator;
import org.hibernate.generator.EventType;

public class SnowflakeGenerator implements BeforeExecutionGenerator {

    private static volatile SnowflakeWorker worker;

    private static SnowflakeWorker getWorker() {
        SnowflakeWorker local = worker;
        if (local == null) {
            synchronized (SnowflakeGenerator.class) {
                local = worker;
                if (local == null) {
                    local = new SnowflakeWorker(
                        SnowflakeConfig.getWorkerId(),
                        SnowflakeConfig.getDatacenterId()
                    );
                    worker = local;
                }
            }
        }
        return local;
    }

    @Override
    public Object generate(
        SharedSessionContractImplementor session,
        Object owner,
        Object currentValue,
        EventType eventType
    ) {
        return getWorker().nextId();
    }

    @Override
    public EnumSet<EventType> getEventTypes() {
        return EnumSet.of(EventType.INSERT);
    }
}
