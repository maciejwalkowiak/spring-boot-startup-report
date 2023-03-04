package com.example.startupevents;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.spring6.view.ThymeleafViewResolver;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.thymeleaf.ThymeleafAutoConfiguration;
import org.springframework.boot.context.metrics.buffering.BufferingApplicationStartup;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@AutoConfigureAfter(ThymeleafAutoConfiguration.class)
@ConditionalOnClass(ObjectMapper.class)
@ConditionalOnBean({SpringTemplateEngine.class, BufferingApplicationStartup.class})
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
    ReportRenderer reportRenderer(TimelineFactory timelineFactory, SpringTemplateEngine templateEngine) {
        return new ReportRenderer(timelineFactory, templateEngine);
    }

    @ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
    @ConditionalOnBean({ThymeleafViewResolver.class})
    @Bean
    StartupEventsController startupEventsController(ReportRenderer reportRenderer) {
        return new StartupEventsController(reportRenderer);
    }
}
