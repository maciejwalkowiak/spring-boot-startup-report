package com.maciejwalkowiak.spring.boot.startup;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.test.context.ContextCustomizer;
import org.springframework.test.context.MergedContextConfiguration;

import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;

/**
 * Adds a listener that generates an HTML report with {@link ReportRenderer} when application context gets closed.
 *
 * @author Maciej Walkowiak
 */
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
                        Path buildDirectory = resolveReportDirectory();
                        Path path = Files.write(buildDirectory.resolve(Paths.get(reportName())), reportRenderer.render().getBytes(StandardCharsets.UTF_8));
                        LOGGER.debug("Report for test {} saved to {}", testName, path.toAbsolutePath());
                    } catch (IOException e) {
                        LOGGER.error("Error during rendering analysis report", e);
                        throw new RuntimeException(e);
                    }
                } catch (NoSuchBeanDefinitionException e) {
                    LOGGER.warn("Report for test {} not generated", testName, e);
                }
            }
        });
    }

    private static Path resolveReportDirectory() throws IOException {
        Path mavenTarget = Paths.get("target");
        Path gradleBuild = Paths.get("build");

        Path buildDirectory;
        if (Files.exists(mavenTarget)) {
            buildDirectory = mavenTarget;
        } else if (Files.exists(gradleBuild)) {
            buildDirectory = gradleBuild;
        } else {
            buildDirectory = Paths.get(".");
        }

        Path reportDirectory = buildDirectory.resolve("startup-reports");
        if (!Files.exists(reportDirectory)) {
            Files.createDirectories(reportDirectory);
        }
        return reportDirectory;
    }

    private String reportName() {
        return "startup-report-" + testName + ".html";
    }

    @Override
    public boolean equals(Object obj) {
        return (obj != null) && (obj.getClass() == getClass());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
