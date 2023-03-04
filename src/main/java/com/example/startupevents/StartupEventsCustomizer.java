package com.example.startupevents;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.test.context.ContextCustomizer;
import org.springframework.test.context.MergedContextConfiguration;

public class StartupEventsCustomizer implements ContextCustomizer {
    private static final Logger LOGGER = LoggerFactory.getLogger(StartupEventsCustomizer.class);
    private final String testName;

    public StartupEventsCustomizer(String name) {
        this.testName = name;
    }

    @Override
    public void customizeContext(ConfigurableApplicationContext context, MergedContextConfiguration mergedConfig) {
        context.addApplicationListener(event -> {
            if (event instanceof ContextClosedEvent) {
                try {
                    ReportRenderer reportRenderer = context.getBean(ReportRenderer.class);
                    try {
                        Path path = Files.writeString(Path.of(getFirst()), reportRenderer.render());
                        LOGGER.debug("Report for test {} saved to {}", testName, path.toAbsolutePath());
                    } catch (IOException e) {
                        LOGGER.error("Error during rendering analysis report", e);
                        throw new RuntimeException(e);
                    }
                } catch (NoSuchBeanDefinitionException e) {
                    LOGGER.warn("Report for test {} not generated. Perhaps Thymeleaf is missing on the classpath?", testName);
                }
            }
        });
    }

    private String getFirst() {
        return "analysis-report-" + testName + ".html";
    }
}
