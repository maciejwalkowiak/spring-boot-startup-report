package com.example.startupevents;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.boot.context.metrics.buffering.BufferingApplicationStartup;
import org.springframework.boot.context.metrics.buffering.StartupTimeline;
import org.springframework.core.metrics.StartupStep;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Controller
public class StartupEventsController {
    private final BufferingApplicationStartup applicationStartup;
    private final TagsResolver resolver;


    public StartupEventsController(BufferingApplicationStartup applicationStartup, TagsResolver resolver) {
        this.applicationStartup = applicationStartup;
        this.resolver = resolver;
    }

    @GetMapping("/startup-analysis")
    String index() {
        return "startup-analysis";
    }

    @GetMapping("/startup")
    @ResponseBody
    List<Event> foo() {
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
            this.tags = StartupEventsController.this.resolver.resolveTags(timelineEvent);
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
