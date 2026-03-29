package com.example.demo.config;

import org.axonframework.extension.spring.config.EventProcessorDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class AxonEventProcessingConfig {

    @Bean
    EventProcessorDefinition accountQueryProcessorDefinition() {
        return EventProcessorDefinition
                .subscribing("com.example.demo.account.query")
                .assigningHandlers(descriptor -> {
                    Class<?> beanType = descriptor.beanType();
                    return beanType != null
                            && beanType.getPackageName().startsWith("com.example.demo.account.query");
                })
                .notCustomized();
    }
}
