package org.crawler.app;

import org.crawler.engine.queue.DemoLocalVisitedCache;
import org.crawler.engine.WebCrawler;
import org.crawler.model.VisitedCache;
import org.slf4j.LoggerFactory;
import org.springframework.context.SmartLifecycle;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class CrawlerExecutor implements SmartLifecycle {

    private final WebCrawler crawler;
    private boolean running = false;

    public CrawlerExecutor(WebCrawler crawler) {
        this.crawler = crawler;
    }

    @Override
    public void start() {
        running = true;
        Thread.ofPlatform().name("crawler-lifecycle").start(crawler::start);
    }

    @Override
    public void stop() {
        crawler.stop();
        printReport();
        running = false;
    }

    @Override
    public boolean isRunning() {
        return running;
    }

    @Override
    public int getPhase() {
        return Integer.MAX_VALUE;
    }

    private void printReport() {
        VisitedCache cache = crawler.getVisitedCache();
        if (cache instanceof DemoLocalVisitedCache d) {
            Set<String> urls = d.getVisitedUrls();
            LoggerFactory.getLogger(getClass()).info("Visited URLs: {}", urls.size());
            urls.forEach(url -> LoggerFactory.getLogger(getClass()).info("{}", url));
        }
    }

}
