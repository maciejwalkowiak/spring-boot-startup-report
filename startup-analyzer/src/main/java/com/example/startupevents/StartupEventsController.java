package com.example.startupevents;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StartupEventsController {
    private final ReportRenderer reportRenderer;

    public StartupEventsController(ReportRenderer reportRenderer) {
        this.reportRenderer = reportRenderer;
    }

    @GetMapping(value = "${startup-events.path:/startup-report}", produces = MediaType.TEXT_HTML_VALUE)
    String index() {
        return reportRenderer.render();
    }
}
