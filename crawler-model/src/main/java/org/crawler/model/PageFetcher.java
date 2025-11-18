package org.crawler.model;

public interface PageFetcher {

    /**
     * Fetches the page at the given URL and returns the parsed result.
     *
     * @param url the URL of the page to fetch
     * @return a {@link CrawlResult} containing the page's content and discovered links
     * @throws Exception if an error occurs during fetching or parsing
     */
    CrawlResult fetch(String url) throws Exception;
}
