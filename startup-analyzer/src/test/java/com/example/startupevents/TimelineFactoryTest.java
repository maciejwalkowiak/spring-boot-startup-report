package com.example.startupevents;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.boot.context.metrics.buffering.BufferingApplicationStartup;
import org.springframework.core.metrics.StartupStep;

public class TimelineFactoryTest {

    @Test
    void generatesTimeline() {
        BufferingApplicationStartup startup = new BufferingApplicationStartup(100);
        startup.startRecording();
        StartupStep event1 = startup.start("event1");
        StartupStep event2 = startup.start("event2");
        event1.end();
        event2.end();
        

        TimelineFactory timelineFactory = new TimelineFactory(startup, new TagsResolver());
        List<Event> events = timelineFactory.getTimeline();
        assertThat(events.get(0).getParentId()).isZero();
        assertThat(events.get(1).getParentId()).isEqualTo(events.get(0).getId());
    }
}
