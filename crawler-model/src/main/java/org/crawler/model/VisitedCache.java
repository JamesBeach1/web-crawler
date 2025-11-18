package org.crawler.model;


public interface VisitedCache {
    boolean isVisited(String url);
    void markVisited(String url, int depthFromSeed);
}
