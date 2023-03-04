package com.example.startupevents;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class StartupEventsController {
    private final ReportRenderer reportRenderer;

    public StartupEventsController(ReportRenderer reportRenderer) {
        this.reportRenderer = reportRenderer;
    }

    @GetMapping("/startup-analysis")
    String index(Model model) {
        model.addAttribute("events", reportRenderer.render());
        return "startup-analysis";
    }
}
