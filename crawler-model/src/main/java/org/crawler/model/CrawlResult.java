package org.crawler.model;

import java.time.Instant;
import java.util.List;

public record CrawlResult(String url, String title, String description, List<String> discoveredUrls, Instant crawledAt) {
}
