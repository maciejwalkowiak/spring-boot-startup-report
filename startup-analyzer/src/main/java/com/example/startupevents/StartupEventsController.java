package com.example.startupevents;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Exposes startup event report over HTTP.
 * 
 * @author Maciej Walkowiak
 */
@RestController
class StartupEventsController {
    private final ReportRenderer reportRenderer;

    public StartupEventsController(ReportRenderer reportRenderer) {
        this.reportRenderer = reportRenderer;
    }

    @GetMapping(value = "${startup-events.path:/startup-report}", produces = MediaType.TEXT_HTML_VALUE)
    String index() {
        return reportRenderer.render();
    }
}
