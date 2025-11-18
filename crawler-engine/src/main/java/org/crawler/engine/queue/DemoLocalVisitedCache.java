package org.crawler.engine.queue;

import org.crawler.model.CrawlUrl;
import org.crawler.model.VisitedCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class DemoLocalVisitedCache implements VisitedCache {

    private static final Logger log = LoggerFactory.getLogger(DemoLocalVisitedCache.class);

    private final Map<String, CrawlUrl> visited = new ConcurrentHashMap<>();
    private final long expirationMillis;

    public DemoLocalVisitedCache(long expirationMillis) {
        this.expirationMillis = expirationMillis;
    }

    @Override
    public boolean isVisited(String url) {
        CrawlUrl crawl = visited.get(url);
        if (crawl == null) return false;

        long now = System.currentTimeMillis();
        if (now - crawl.lastCrawlMillis() > expirationMillis) {
            visited.remove(url);
            return false;
        }
        return true;
    }

    @Override
    public void markVisited(String url, int depthFromSeed) {
        visited.computeIfAbsent(url, u -> CrawlUrl.create(u, depthFromSeed));
    }

    public Set<CrawlUrl> getVisitedUrls() {
        return new HashSet<>(visited.values());
    }

}
