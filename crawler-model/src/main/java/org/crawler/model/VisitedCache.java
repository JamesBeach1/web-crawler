package org.crawler.model;

import java.util.Set;

public interface VisitedCache {
    boolean isVisited(String url);
    void markVisited(String url);
}
