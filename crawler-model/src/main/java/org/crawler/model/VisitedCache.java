package org.crawler.model;


public interface VisitedCache {

    /**
     * Checks whether the given URL has already been visited.
     *
     * @param url the URL to check
     * @return {@code true} if the URL has been visited, {@code false} otherwise
     */
    boolean isVisited(String url);

    /**
     * Marks the given URL as visited.
     *
     * @param url the URL to mark as visited
     * @param depthFromSeed the depth of this URL from the initial seed URL
     */
    void markVisited(String url, int depthFromSeed);
}
