package com.maciejwalkowiak.spring.boot.startup;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.StreamUtils;

/**
 * Renders the startup event report to an string containing complete HTML page.
 *
 * @author Maciej Walkowiak
 */
class ReportRenderer {
    private final TimelineFactory timelineFactory;
    private final ResourceLoader resourceLoader;
    private final ObjectMapper mapper = new ObjectMapper();

    public ReportRenderer(TimelineFactory timelineFactory, ResourceLoader resourceLoader) {
        this.timelineFactory = timelineFactory;
        this.resourceLoader = resourceLoader;
    }

    String render() {
        Resource resource = resourceLoader.getResource("classpath:/com/maciejwalkowiak/spring/boot/startup/startup-analysis.html");
        try {
            String template = StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);
            return template.replace("%events%", serialize(timelineFactory.getTimeline()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String serialize(List<Event> events) {
        try {
            return mapper.writeValueAsString(events);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
