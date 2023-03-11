package com.maciejwalkowiak.spring.boot.startup;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.boot.context.metrics.buffering.BufferingApplicationStartup;
import org.springframework.core.metrics.StartupStep;

public class TimelineFactoryTest {
    private final BufferingApplicationStartup startup = new BufferingApplicationStartup(100);
    private final TimelineFactory timelineFactory = new TimelineFactory(startup, new TagsResolver());

    @Test
    void generatesTimelineWithMultipleTopElements() {
        // given
        startup.startRecording();
        StartupStep step1 = startup.start("event1");
        StartupStep step2 = startup.start("event2");
        step1.end();
        step2.end();
        StartupStep step3 = startup.start("event3");
        step3.end();

        // when
        List<Event> events = timelineFactory.getTimeline();

        // then
        assertThat(events).hasSize(2);
        assertThat(events).anySatisfy(it -> {
            assertThat(it.getParentId()).isNull();
            assertThat(it.getId()).isZero();
            assertThat(it.getName()).isEqualTo("event1");
            assertThat(it.getChildren())
                    .hasSize(1)
                    .singleElement()
                    .satisfies(child -> {
                        assertThat(child.getName()).isEqualTo("event2");
                        assertThat(child.getId()).isEqualTo(1);
                        assertThat(child.getParentId()).isEqualTo(it.getId());
                    });
        });
        assertThat(events).anySatisfy(it -> {
            assertThat(it.getId()).isEqualTo(2);
            assertThat(it.getName()).isEqualTo("event3");
            assertThat(it.getParentId()).isNull();
        });
    }

    @Test
    void generatesTimelineWithSingleTopElement() {
        // given
        startup.startRecording();
        StartupStep step1 = startup.start("event1");
        StartupStep step2 = startup.start("event2");
        step2.end();
        StartupStep step3 = startup.start("event3");
        step3.end();
        step1.end();

        // when
        List<Event> events = timelineFactory.getTimeline();

        // then
        assertThat(events).hasSize(2);
        assertThat(events).anySatisfy(it -> {
            assertThat(it.getName()).isEqualTo("event2");
            assertThat(it.getParentId()).isNotNull();
            assertThat(it.getId()).isEqualTo(1);
        });
        assertThat(events).anySatisfy(it -> {
            assertThat(it.getName()).isEqualTo("event3");
            assertThat(it.getParentId()).isNotNull();
            assertThat(it.getId()).isEqualTo(2);
        });
        assertThat(events).noneSatisfy(it -> assertThat(it.getName()).isEqualTo("event1"));
    }
}
