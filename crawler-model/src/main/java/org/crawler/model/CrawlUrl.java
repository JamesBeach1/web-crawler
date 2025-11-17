package org.crawler.model;

import java.time.Instant;

public record CrawlUrl(String url, int depthFromSeed, Instant lastCrawl) {
}
