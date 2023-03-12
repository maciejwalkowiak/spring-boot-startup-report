package com.maciejwalkowiak.spring.boot.startup;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.boot.context.metrics.buffering.BufferingApplicationStartup;
import org.springframework.boot.context.metrics.buffering.StartupTimeline;

class TimelineFactory {
    private final BufferingApplicationStartup applicationStartup;
    private final TagsResolver tagsResolver;

    public TimelineFactory(BufferingApplicationStartup applicationStartup, TagsResolver resolver) {
        this.applicationStartup = applicationStartup;
        this.tagsResolver = resolver;
    }

    public List<Event> getTimeline() {
        // sort from longest to shortest
        List<StartupTimeline.TimelineEvent> timelineEvents = applicationStartup.getBufferedTimeline()
                .getEvents()
                .stream()
                .sorted(Collections.reverseOrder(
                        Comparator.comparingLong((StartupTimeline.TimelineEvent o) -> o.getDuration().toMillis())))
                .collect(Collectors.toList());

        // create a hierarchical structure
        return timelineEvents.stream()
                .map(it -> Event.create(it, timelineEvents, tagsResolver))
                .sorted(Collections.reverseOrder(Comparator.comparingLong(Event::getValue)))
                .filter(it -> it.getParentId() == null)
                .collect(Collectors.toList());
    }
}
