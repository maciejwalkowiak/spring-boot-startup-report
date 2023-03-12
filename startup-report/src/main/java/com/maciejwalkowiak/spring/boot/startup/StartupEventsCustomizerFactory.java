package com.maciejwalkowiak.spring.boot.startup;

import java.util.List;

import org.springframework.test.context.ContextConfigurationAttributes;
import org.springframework.test.context.ContextCustomizer;
import org.springframework.test.context.ContextCustomizerFactory;

/**
 * Creates a {@link StartupEventsCustomizer} to customize application context in integration tests.
 * 
 * @author Maciej Walkowiak
 */
public class StartupEventsCustomizerFactory implements ContextCustomizerFactory {
    @Override public ContextCustomizer createContextCustomizer(Class<?> testClass,
            List<ContextConfigurationAttributes> configAttributes) {
        return new StartupEventsCustomizer(testClass.getName());
    }
}
