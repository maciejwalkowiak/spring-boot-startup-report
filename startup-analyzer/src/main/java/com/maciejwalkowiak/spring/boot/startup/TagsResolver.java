package com.maciejwalkowiak.spring.boot.startup;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.boot.context.metrics.buffering.StartupTimeline;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.metrics.StartupStep;
import org.springframework.core.metrics.StartupStep.Tags;

/**
 * Resolves tags from {@link StartupTimeline.TimelineEvent} tags by getting more details from the {@link ApplicationContext}.
 *
 * @author Maciej Walkowiak
 */
class TagsResolver implements ApplicationContextAware {
    private static final Logger LOGGER = LoggerFactory.getLogger(TagsResolver.class);
    private ApplicationContext ctx;

    Map<String, String> resolveTags(StartupTimeline.TimelineEvent timelineEvent) {
        Map<String, String> tags = toHashMap(timelineEvent.getStartupStep().getTags());

        if (ctx == null) {
            LOGGER.warn("Something is wrong, ApplicationContext is not set");
            return tags;
        }

        if (tags.containsKey("beanName")) {
            try {
                Object bean = ctx.getBean(tags.get("beanName"));
                Class<?> clazz = bean.getClass();
                tags.put("class", clazz.getName());
                if (bean.getClass().getAnnotations().length > 0) {
                    tags.put("annotations", Arrays.stream(bean.getClass().getAnnotations())
                            .map(a -> "@" + a.annotationType().getSimpleName()).collect(Collectors.joining(",")));
                }
            } catch (NoSuchBeanDefinitionException e) {
                LOGGER.debug("No bean with name {} found", tags.get("beanName"), e);
            }

        }
        return tags;
    }

    private static Map<String, String> toHashMap(Tags tags) {
        return new HashMap<>(StreamSupport.stream(tags.spliterator(), false)
                .collect(Collectors.toMap(StartupStep.Tag::getKey, StartupStep.Tag::getValue)));
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.ctx = applicationContext;
    }
}
