package com.snowk.blog.api.newsletter;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class NewsletterJobs {
    private final NewsletterDeliveryService delivery;
    private final NewsletterService service;

    public NewsletterJobs(NewsletterDeliveryService delivery, NewsletterService service) {
        this.delivery = delivery;
        this.service = service;
    }

    @Scheduled(initialDelay = 30000, fixedDelay = 10000)
    public void dispatch() {
        delivery.expandOneCampaign();
        delivery.deliverOne();
    }

    @Scheduled(initialDelay = 120000, fixedDelay = 3600000)
    public void retention() {
        service.prune();
    }
}
