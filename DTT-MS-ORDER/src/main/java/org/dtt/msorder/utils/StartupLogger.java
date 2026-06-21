package org.dtt.msorder.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class StartupLogger {
    @Value("${server.port}")
    private String port;
    @Value("${spring.application.name}")
    private String appName;
    @EventListener(ApplicationReadyEvent.class)
    public void logStartup() {
        log.info(
                "Application started successfully | MS: {} | Port: {}",
                appName,
                port
        );
    }
}
