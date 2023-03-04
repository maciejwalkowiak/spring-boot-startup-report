package com.example.startupevents;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.metrics.buffering.BufferingApplicationStartup;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
public class StartupEventsAutoConfiguration {

    @Bean
    TagsResolver tagsResolver() {
        return new TagsResolver();
    }

    @ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
    @ConditionalOnBean(BufferingApplicationStartup.class)
    @Bean
    StartupEventsController startupEventsController(BufferingApplicationStartup startup, TagsResolver tagsResolver) {
        return new StartupEventsController(startup, tagsResolver);
    }
}
