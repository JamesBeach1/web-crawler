package org.crawler.model;

public interface PageFetcher {
    CrawlResult fetch(String url) throws Exception;
}
