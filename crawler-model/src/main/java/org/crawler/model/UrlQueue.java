package org.crawler.model;

import java.util.Optional;

public interface UrlQueue {
    Optional<CrawlUrl> poll();
    void offer(CrawlUrl url);
}
