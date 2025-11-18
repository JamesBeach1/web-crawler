package org.crawler.app.reporting;

import org.crawler.app.CrawlerExecutor;
import org.crawler.engine.queue.DemoLocalVisitedCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Profile("local")
public class DemoCacheHealthReporter {

    private static final Logger log = LoggerFactory.getLogger(DemoCacheHealthReporter.class);

    private final DemoLocalVisitedCache visitedCache;

    public DemoCacheHealthReporter(DemoLocalVisitedCache visitedCache) {
        this.visitedCache = visitedCache;
    }

    @Scheduled(fixedRateString = "${crawler.health.report.interval.millis}")
    public void reportHealth() {
        int size = visitedCache.getVisitedUrls().size();
        log.info("VisitedCache health: {} URLs visited", size);
    }
}
