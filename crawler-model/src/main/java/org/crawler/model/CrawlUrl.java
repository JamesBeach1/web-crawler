package org.crawler.model;


public record CrawlUrl(String url, int depthFromSeed, long lastCrawlMillis) {
    public static CrawlUrl create(String url, int depthFromSeed) {
        return new CrawlUrl(url, depthFromSeed, System.currentTimeMillis());
    }
}
