package org.crawler.app.config;

import org.crawler.engine.queue.DemoLocalQueue;
import org.crawler.engine.queue.DemoLocalVisitedCache;
import org.crawler.engine.StreamingHtmlPageFetcher;
import org.crawler.engine.WebCrawler;
import org.crawler.model.CrawlUrl;
import org.crawler.model.PageFetcher;
import org.crawler.model.UrlQueue;
import org.crawler.model.VisitedCache;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.Set;


@Configuration
@Profile("local")
public class LocalCrawlerConfig {

    @Value("${crawler.concurrency.limit}")
    private int CONCURRENCY_LIMIT;

    @Value("${crawler.client.pool.size}")
    private int CLIENT_POOL_SIZE;

    @Value("${crawler.connect.timeout.millis}")
    private Long CONNECT_TIMEOUT_MS;

    @Value("${crawler.restrict.to.subdomain}")
    private boolean restrictToSubDomain;

    @Value("${crawler.cache.expiration.millis}")
    private long cacheExpirationMillis;

    @Bean
    public UrlQueue urlQueue() {
        CrawlUrl seed = CrawlUrl.create("https://crawlme.monzo.com/", 0);
        return new DemoLocalQueue(Set.of(seed));
    }

    @Bean
    public VisitedCache visitedCache() {
        return new DemoLocalVisitedCache(cacheExpirationMillis);
    }

    @Bean
    public WebCrawler webCrawler(PageFetcher pageFetcher, UrlQueue urlQueue, VisitedCache visitedCache) {
        return new WebCrawler(pageFetcher, urlQueue, visitedCache, true, CONCURRENCY_LIMIT);
    }

    @Bean
    public PageFetcher pageFetcher() {
        return new StreamingHtmlPageFetcher(CLIENT_POOL_SIZE, CONNECT_TIMEOUT_MS);
    }
}