package org.crawler.model;

import java.util.List;

public record CrawlResult(String url, String title, String description, List<String> discoveredUrls, long crawledAtMillis) {
    public static CrawlResult create(String url, String title, String description, List<String> discoveredUrls) {
        return new CrawlResult(url, title, description, discoveredUrls, System.currentTimeMillis());
    }
}
