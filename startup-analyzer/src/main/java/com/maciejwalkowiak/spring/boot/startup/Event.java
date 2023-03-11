package com.maciejwalkowiak.spring.boot.startup;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.boot.context.metrics.buffering.StartupTimeline;
import org.springframework.boot.context.metrics.buffering.StartupTimeline.TimelineEvent;

class Event {
    private final Long id;
    private final Long parentId;
    private final String label;
    private final long value;
    private final long actualDuration;
    private final Map<String, String> tags;
    private final List<Event> children;

    static Event create(StartupTimeline.TimelineEvent timelineEvent, List<StartupTimeline.TimelineEvent> allEvents, TagsResolver tagsResolver) {
        List<TimelineEvent> children = findChildren(timelineEvent.getStartupStep().getId(), allEvents);;

        List<Event> eventChildren = children == null ? Collections.emptyList() : children.stream().map(c -> create(c, allEvents, tagsResolver)).collect(Collectors.toList());

        return new Event(eventChildren,
            timelineEvent.getStartupStep().getId(),
            timelineEvent.getStartupStep().getParentId(),
            timelineEvent.getStartupStep().getName(),
            timelineEvent.getDuration().toMillis(),
            timelineEvent.getDuration().toMillis() - eventChildren.stream().map(Event::getValue).reduce(0L, Long::sum),
            tagsResolver.resolveTags(timelineEvent)
        );
    }

    private static List<StartupTimeline.TimelineEvent> findChildren(Long parentId, List<StartupTimeline.TimelineEvent> allEvents) {
        return allEvents.stream().filter(it -> parentId.equals(it.getStartupStep().getParentId())).collect(Collectors.toList());
    }

    public Event(List<Event> children, Long id, Long parentId, String label, long value, long actualDuration,
            Map<String, String> tags) {
        this.children = children;
        this.id = id;
        this.parentId = parentId;
        this.label = label;
        this.value = value;
        this.actualDuration = actualDuration;
        this.tags = tags;
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
