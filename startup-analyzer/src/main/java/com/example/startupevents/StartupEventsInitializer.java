
package com.example.startupevents;

import org.springframework.boot.context.metrics.buffering.BufferingApplicationStartup;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.metrics.ApplicationStartup;

public class StartupEventsInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        if (applicationContext.getApplicationStartup() == ApplicationStartup.DEFAULT) {
            applicationContext.setApplicationStartup(new BufferingApplicationStartup(10_000));
        }
    }
}
