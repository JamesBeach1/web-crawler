package org.crawler.app.config;

import org.crawler.engine.queue.DemoLocalQueue;
import org.crawler.engine.queue.DemoLocalVisitedCache;
import org.crawler.engine.StreamingHtmlPageFetcher;
import org.crawler.engine.WebCrawler;
import org.crawler.model.CrawlUrl;
import org.crawler.model.PageFetcher;
import org.crawler.model.UrlQueue;
import org.crawler.model.VisitedCache;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.time.Instant;
import java.util.Set;


@Configuration
@Profile("local")
public class LocalCrawlerConfig {

    private final static int CONCURRENCY_LIMIT = 50;

    @Bean
    public UrlQueue urlQueue() {
        CrawlUrl seed = new CrawlUrl("https://crawlme.monzo.com/", 0, Instant.now());
        return new DemoLocalQueue(Set.of(seed));
    }

    @Bean
    public VisitedCache visitedCache() {
        return new DemoLocalVisitedCache();
    }

    @Bean
    public WebCrawler webCrawler(PageFetcher pageFetcher, UrlQueue urlQueue, VisitedCache visitedCache) {
        return new WebCrawler(pageFetcher, urlQueue, visitedCache, true, CONCURRENCY_LIMIT);
    }

    @Bean
    public PageFetcher pageFetcher() {
        return new StreamingHtmlPageFetcher();
    }
}