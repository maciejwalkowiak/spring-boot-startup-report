package com.example.startupevents;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
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
                .collect(Collectors.toList());

//        var map = events.stream()
//                .collect(Collectors.groupingBy(timelineEvent -> Optional.ofNullable(timelineEvent.getStartupStep().getParentId()).orElse(-1L), Collectors.mapping(timelineEvent -> timelineEvent, Collectors.toList())));

        return events.stream()
                .map(it -> new Event(it, findChildren(it.getStartupStep().getId(), events), events))
//                .filter(it -> it.parentId == null || it.children.size() > 0)
                .sorted(Collections.reverseOrder(Comparator.comparingLong((Event o) -> o.value)))
                .collect(Collectors.toList());
    }

    private static List<StartupTimeline.TimelineEvent> findChildren(Long parentId, List<StartupTimeline.TimelineEvent> allEvents) {
        return allEvents.stream().filter(it -> parentId.equals(it.getStartupStep().getParentId())).collect(Collectors.toList());
    }

    class Event {
        private final StartupTimeline.TimelineEvent timelineEvent;
        private final List<Event> children;

        private final Long id;
        private final Long parentId;
        private final String label;
        private final long value;
        private final long actualDuration;
        private final Map<String, String> tags;

        public Event(StartupTimeline.TimelineEvent timelineEvent, List<StartupTimeline.TimelineEvent> children,
                List<StartupTimeline.TimelineEvent> allEvents) {
            this.id = timelineEvent.getStartupStep().getId();
            this.parentId = timelineEvent.getStartupStep().getParentId();
            this.timelineEvent = timelineEvent;
            this.children = children == null ? Collections.emptyList() : children.stream().map(c -> new Event(c, findChildren(c.getStartupStep().getId(), allEvents), allEvents)).collect(
                    Collectors.toList());
            this.label = timelineEvent.getStartupStep().getName();
            this.value = timelineEvent.getDuration().toMillis();
            this.actualDuration = timelineEvent.getDuration().toMillis() - this.children.stream().map(Event::getValue).reduce(0L, Long::sum);
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

        public long getActualDuration() {
            return actualDuration;
        }
    }
}
