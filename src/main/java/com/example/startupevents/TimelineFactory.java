package com.example.startupevents;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonIgnore;

import org.springframework.boot.context.metrics.buffering.BufferingApplicationStartup;
import org.springframework.boot.context.metrics.buffering.StartupTimeline;

public class TimelineFactory {
    private final BufferingApplicationStartup applicationStartup;
    private final TagsResolver resolver;

    public TimelineFactory(BufferingApplicationStartup applicationStartup, TagsResolver resolver) {
        this.applicationStartup = applicationStartup;
        this.resolver = resolver;
    }

    public List<Event> getTimeline() {
        List<StartupTimeline.TimelineEvent> events = applicationStartup.getBufferedTimeline()
                .getEvents()
                .stream()
                .sorted(Collections.reverseOrder(Comparator.comparingLong((StartupTimeline.TimelineEvent o) -> o.getDuration().toMillis())))
                .toList();

        var map = events.stream()
                .collect(Collectors.groupingBy(timelineEvent -> Optional.ofNullable(timelineEvent.getStartupStep().getParentId()).orElse(-1L), Collectors.mapping(timelineEvent -> timelineEvent, Collectors.toList())));

        return events.stream()
                .map(it -> new Event(it, findChildren(it.getStartupStep().getId(), map), map))
                .sorted(Collections.reverseOrder(Comparator.comparingLong((Event o) -> o.value)))
                .toList();
    }

    private static List<StartupTimeline.TimelineEvent> findChildren(Long parentId, Map<Long, List<StartupTimeline.TimelineEvent>> allEvents) {
        return allEvents.get(parentId);
    }

    class Event {
        private final StartupTimeline.TimelineEvent timelineEvent;
        private final List<Event> children;

        private final Long id;
        private final Long parentId;
        private final String label;
        private final long value;
        private final Map<String, String> tags;

        public Event(StartupTimeline.TimelineEvent timelineEvent, List<StartupTimeline.TimelineEvent> children,
                Map<Long, List<StartupTimeline.TimelineEvent>> allEvents) {
            this.id = timelineEvent.getStartupStep().getId();
            this.parentId = timelineEvent.getStartupStep().getParentId();
            this.timelineEvent = timelineEvent;
            this.children = children == null ? Collections.emptyList() : children.stream().map(c -> new Event(c, findChildren(c.getStartupStep().getId(), allEvents), allEvents)).toList();
            this.label = timelineEvent.getStartupStep().getName();
            this.value = timelineEvent.getDuration().toMillis();
            this.tags = resolver.resolveTags(timelineEvent);
        }

        public String getLabel() {
            return label;
        }

        public long getValue() {
            return value;
        }

        public Long getId() {
            return id;
        }

        public Long getParentId() {
            return parentId;
        }

        @JsonIgnore
        public StartupTimeline.TimelineEvent getTimelineEvent() {
            return timelineEvent;
        }

        public List<Event> getChildren() {
            return children;
        }

        public Map<String, String> getTags() {
            return tags;
        }
    }
}
