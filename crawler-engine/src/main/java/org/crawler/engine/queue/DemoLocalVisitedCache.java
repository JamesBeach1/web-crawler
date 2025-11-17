package org.crawler.engine.queue;

import org.crawler.model.VisitedCache;

import java.util.concurrent.ConcurrentHashMap;

public class DemoLocalVisitedCache implements VisitedCache {
    private final ConcurrentHashMap<String, Boolean> visited = new ConcurrentHashMap<>();

    @Override
    public boolean isVisited(String url) {
        return visited.containsKey(url);
    }

    @Override
    public void markVisited(String url) {
        visited.put(url, true);
    }
}
