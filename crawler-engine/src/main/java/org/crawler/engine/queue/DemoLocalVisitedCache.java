package org.crawler.engine.queue;

import org.crawler.model.VisitedCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class DemoLocalVisitedCache implements VisitedCache {

    private static final Logger log = LoggerFactory.getLogger(DemoLocalVisitedCache.class);

    private final Set<String> visited = ConcurrentHashMap.newKeySet();

    @Override
    public boolean isVisited(String url) {
        return visited.contains(url);
    }

    @Override
    public void markVisited(String url) {
        log.debug("Marking URL as visited: {}", url);
        visited.add(url);
    }

    public Set<String> getVisitedUrls() {
        return visited;
    }

}
