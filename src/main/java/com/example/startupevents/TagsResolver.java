package com.example.startupevents;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.beans.BeansException;
import org.springframework.boot.context.metrics.buffering.StartupTimeline;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.annotation.MergedAnnotations;
import org.springframework.core.metrics.StartupStep;
import org.springframework.stereotype.Component;

//@Component
public class TagsResolver implements ApplicationContextAware {
    private ApplicationContext ctx;

    Map<String, String> resolveTags(StartupTimeline.TimelineEvent timelineEvent) {
        Map<String, String> collect = new HashMap<>(StreamSupport.stream(timelineEvent.getStartupStep().getTags().spliterator(), false)
                .collect(Collectors.toMap(StartupStep.Tag::getKey, StartupStep.Tag::getValue)));
        if (collect.containsKey("beanName")) {
            Object bean = ctx.getBean(collect.get("beanName"));
            Class<?> clazz = bean.getClass();
            collect.put("class", clazz.getName());
            if (bean.getClass().getAnnotations().length > 0) {
                collect.put("annotations", Arrays.stream(bean.getClass().getAnnotations()).map(a -> "@" + a.annotationType().getSimpleName()).collect(Collectors.joining(",")));
            }
        }
        return collect;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.ctx = applicationContext;
    }
}
