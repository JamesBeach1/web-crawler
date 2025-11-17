package org.crawler.app;


import jakarta.annotation.PreDestroy;
import org.crawler.engine.queue.DemoLocalVisitedCache;
import org.crawler.engine.queue.WebCrawler;
import org.crawler.model.VisitedCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicBoolean;

@Component
public class CrawlerExecutor {

    private static final Logger log = LoggerFactory.getLogger(CrawlerExecutor.class);

    private final WebCrawler crawler;
    private final AtomicBoolean started = new AtomicBoolean(false);

    public CrawlerExecutor(WebCrawler crawler) {
        this.crawler = crawler;
        startCrawler();
    }

    private void startCrawler() {
        if (started.compareAndSet(false, true)) {
            log.info("Starting WebCrawler...");
            crawler.start();
        }
    }

    @PreDestroy
    public void shutdownCrawler() {
        log.info("Stopping WebCrawler...");
        crawler.stop();

        VisitedCache cache = crawler.getVisitedCache();
        if (cache instanceof DemoLocalVisitedCache) {
            log.info("Crawler shutdown report:");
            log.info("Visited URLs: {}", ((DemoLocalVisitedCache) cache).getVisitedUrls().size());
            ((DemoLocalVisitedCache) cache).getVisitedUrls()
                    .forEach(url -> log.info("{}", url));
        }
    }

}
