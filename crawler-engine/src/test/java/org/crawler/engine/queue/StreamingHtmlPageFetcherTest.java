package org.crawler.engine.queue;


import org.crawler.engine.StreamingHtmlPageFetcher;
import org.crawler.model.CrawlResult;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class StreamingHtmlPageFetcherTest {

    private final StreamingHtmlPageFetcher fetcher = new StreamingHtmlPageFetcher();

    @Test
    void fetch_realPage_shouldExtractHrefsAndMetadata() throws Exception {
        String url = "https://crawlme.monzo.com/";
        CrawlResult result = fetcher.fetch(url);

        System.out.println("Title: " + result.title());
        System.out.println("Description: " + result.description());
        System.out.println("Hrefs: " + result.discoveredUrls());
        System.out.println("Crawled At: " + result.crawledAt());

        assertNotNull(result.title());
        assertNotNull(result.discoveredUrls());
        assertFalse(result.discoveredUrls().isEmpty());
        assertNotNull(result.crawledAt());
    }

}
