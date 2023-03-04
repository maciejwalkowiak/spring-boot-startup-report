package com.example.startupevents;

import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

public class ReportRenderer {
    private final TimelineFactory timelineFactory;
    private final SpringTemplateEngine templateEngine;
    private final ObjectMapper mapper = new ObjectMapper();

    public ReportRenderer(TimelineFactory timelineFactory, SpringTemplateEngine templateEngine) {
        this.timelineFactory = timelineFactory;
        this.templateEngine = templateEngine;
    }

    String render() {
        Context context = new Context();
        context.setVariable("events", serialize(timelineFactory.getTimeline()));
        return templateEngine.process("startup-analysis", context);
    }

    public String serialize(List<TimelineFactory.Event> events) {
        try {
            return mapper.writeValueAsString(events);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
