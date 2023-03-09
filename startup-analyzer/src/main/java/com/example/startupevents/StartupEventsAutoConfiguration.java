package com.example.startupevents;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.metrics.buffering.BufferingApplicationStartup;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ResourceLoader;

@AutoConfiguration
@ConditionalOnClass(ObjectMapper.class)
@ConditionalOnBean({BufferingApplicationStartup.class})
public class StartupEventsAutoConfiguration {

    @Bean
    TagsResolver tagsResolver() {
        return new TagsResolver();
    }

    @Bean
    TimelineFactory timelineFactory(BufferingApplicationStartup applicationStartup, TagsResolver resolver) {
        return new TimelineFactory(applicationStartup, resolver);
    }

    @Bean
    ReportRenderer reportRenderer(TimelineFactory timelineFactory, ResourceLoader resourceLoader) {
        return new ReportRenderer(timelineFactory, resourceLoader);
    }

    @ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
    @Bean
    StartupEventsController startupEventsController(ReportRenderer reportRenderer) {
        return new StartupEventsController(reportRenderer);
    }
}
