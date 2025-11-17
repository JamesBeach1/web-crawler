package org.crawler.engine.queue;

import org.crawler.model.VisitedCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class DemoLocalVisitedCache implements VisitedCache {

    private static final Logger log = LoggerFactory.getLogger(DemoLocalVisitedCache.class);

    private final ConcurrentHashMap<String, Boolean> visited = new ConcurrentHashMap<>();

    @Override
    public boolean isVisited(String url) {
        return visited.containsKey(url);
    }

    @Override
    public void markVisited(String url) {
        log.info("Marking URL as visited: {}", url);
        visited.put(url, true);
    }

    public Set<String> getVisitedUrls() {
        return visited.keySet();
    }

}
