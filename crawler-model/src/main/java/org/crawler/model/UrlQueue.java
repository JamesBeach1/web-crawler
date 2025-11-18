package org.crawler.model;

import java.util.Optional;

public interface UrlQueue {

    /**
     * Retrieves and removes the next URL to be crawled, if available.
     *
     * @return an {@link Optional} containing the next {@link CrawlUrl} if present, or empty if the queue is empty
     */
    Optional<CrawlUrl> poll();

    /**
     * Adds a URL to the queue for future crawling.
     *
     * @param url the {@link CrawlUrl} to add
     */
    void offer(CrawlUrl url);
}
