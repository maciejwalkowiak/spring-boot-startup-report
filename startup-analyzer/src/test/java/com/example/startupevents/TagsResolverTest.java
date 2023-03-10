package com.example.startupevents;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.mockito.Answers;
import org.mockito.Mockito;
import org.springframework.boot.context.metrics.buffering.StartupTimeline.TimelineEvent;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.core.metrics.StartupStep.Tag;
import org.springframework.core.metrics.StartupStep.Tags;

import com.demo.FooService;

public class TagsResolverTest {

    private final TagsResolver tagsResolver = new TagsResolver();

    @Test
    void returnsSimpleTagsWhenApplicationContextNotSet() {
        var timelineEvent = Mockito.mock(TimelineEvent.class, Answers.RETURNS_DEEP_STUBS);
        when(timelineEvent.getStartupStep().getTags()).thenReturn(new TagsContainer(new SimpleTag("key", "val")));
        
        Map<String,String> result = tagsResolver.resolveTags(timelineEvent);

        assertThat(result).containsEntry("key", "val");
    }

    @Test
    void returnsTagWithClassName() {
        var timelineEvent = Mockito.mock(TimelineEvent.class, Answers.RETURNS_DEEP_STUBS);
        when(timelineEvent.getStartupStep().getTags()).thenReturn(new TagsContainer(new SimpleTag("beanName", "fooService")));

        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(AppConfig.class);
        tagsResolver.setApplicationContext(ctx);

        Map<String,String> result = tagsResolver.resolveTags(timelineEvent);

        assertThat(result)
            .containsEntry("beanName", "fooService")
            .containsEntry("class", "com.demo.FooService");
    }

    @Test
    void returnsTagWithAnnotations() {
        var timelineEvent = Mockito.mock(TimelineEvent.class, Answers.RETURNS_DEEP_STUBS);
        when(timelineEvent.getStartupStep().getTags()).thenReturn(new TagsContainer(new SimpleTag("beanName", "fooService")));

        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(AppConfig.class);
        tagsResolver.setApplicationContext(ctx);

        Map<String,String> result = tagsResolver.resolveTags(timelineEvent);

        assertThat(result)
            .containsEntry("annotations", "@Qualifier");
    }

    static class AppConfig {

        @Bean
        FooService fooService() {
            return new FooService();
        }
    }

    static class TagsContainer implements Tags {
        private final List<Tag> tags;

        public TagsContainer(Tag ... tags) {
            this.tags = Arrays.asList(tags);
        }

        @Override
        public Iterator<Tag> iterator() {
            return tags.iterator();
        }

    }

    static class SimpleTag implements Tag {
        private final String key;
        private final String value;

        public SimpleTag(String key, String value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public String getKey() {
            return key;
        }

        @Override
        public String getValue() {
            return value;
        }

    }
}
