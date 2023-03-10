package com.example.startupevents;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.boot.context.metrics.buffering.BufferingApplicationStartup;
import org.springframework.core.metrics.StartupStep;

public class TimelineFactoryTest {

    @Test
    void generatesTimelineWithMultipleTopElements() {
        BufferingApplicationStartup startup = new BufferingApplicationStartup(100);
        startup.startRecording();
        StartupStep step1 = startup.start("event1");
        StartupStep step2 = startup.start("event2");
        step1.end();
        step2.end();
        StartupStep step3 = startup.start("event3");
        step3.end();

        TimelineFactory timelineFactory = new TimelineFactory(startup, new TagsResolver());
        List<Event> events = timelineFactory.getTimeline();

        assertThat(events).hasSize(2);

        assertThat(events).anySatisfy(it -> {
            assertThat(it.getParentId()).isNull();
            assertThat(it.getId()).isZero();
            assertThat(it.getLabel()).isEqualTo("event1");
            assertThat(it.getChildren())
                    .hasSize(1)
                    .singleElement()
                    .satisfies(child -> {
                        assertThat(child.getLabel()).isEqualTo("event2");
                        assertThat(child.getId()).isEqualTo(1);
                        assertThat(child.getParentId()).isEqualTo(it.getId());
                    });
        });

        assertThat(events).anySatisfy(it -> {
            assertThat(it.getId()).isEqualTo(2);
            assertThat(it.getLabel()).isEqualTo("event3");
            assertThat(it.getParentId()).isNull();
        });
    }

    @Test
    void generatesTimelineWithSingleTopElement() {
        BufferingApplicationStartup startup = new BufferingApplicationStartup(100);
        startup.startRecording();
        StartupStep step1 = startup.start("event1");
        StartupStep step2 = startup.start("event2");
        step2.end();
        StartupStep step3 = startup.start("event3");
        step3.end();
        step1.end();

        TimelineFactory timelineFactory = new TimelineFactory(startup, new TagsResolver());
        List<Event> events = timelineFactory.getTimeline();

        assertThat(events).hasSize(2);

        assertThat(events).anySatisfy(it -> {
            assertThat(it.getLabel()).isEqualTo("event2");
            assertThat(it.getParentId()).isNotNull();
            assertThat(it.getId()).isEqualTo(1);
        });

        assertThat(events).anySatisfy(it -> {
            assertThat(it.getLabel()).isEqualTo("event3");
            assertThat(it.getParentId()).isNotNull();
            assertThat(it.getId()).isEqualTo(2);
        });

        assertThat(events).noneSatisfy(it -> assertThat(it.getLabel()).isEqualTo("event1"));
    }
}
