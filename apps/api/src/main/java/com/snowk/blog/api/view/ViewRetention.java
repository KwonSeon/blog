package com.snowk.blog.api.view;
import java.time.*;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
@Configuration
@EnableScheduling
public class ViewRetention {
    private final ViewService service;
    public ViewRetention(ViewService service) {this.service=service;}
    @Scheduled(initialDelay=60000,fixedDelay=3600000)
    public void prune() {service.prune(LocalDate.now(ZoneOffset.UTC));}
}
