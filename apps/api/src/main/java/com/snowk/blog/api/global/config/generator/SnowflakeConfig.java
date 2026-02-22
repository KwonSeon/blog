package com.snowk.blog.api.global.config.generator;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class SnowflakeConfig {

    @Getter
    private static volatile long workerId = 1L;
    @Getter
    private static volatile long datacenterId = 1L;

    public SnowflakeConfig(
        @Value("${snowflake.worker-id}") long workerId,
        @Value("${snowflake.datacenter-id}") long datacenterId
    ) {
        SnowflakeConfig.workerId = workerId;
        SnowflakeConfig.datacenterId = datacenterId;
    }

}

