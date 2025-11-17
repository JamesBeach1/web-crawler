package org.crawler.engine;

import org.crawler.engine.queue.DemoLocalVisitedCache;
import org.crawler.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class WebCrawler {

    private static final Logger log = LoggerFactory.getLogger(WebCrawler.class);

    private final PageFetcher pageFetcher;
    private final UrlQueue urlQueue;
    private final VisitedCache visitedCache;
    private final ExecutorService executor;
    private final AtomicBoolean running = new AtomicBoolean(false);
    private final boolean restrictToSameSubdomain;

    private final Semaphore concurrencyLimiter;

    public WebCrawler(
            PageFetcher pageFetcher,
            UrlQueue urlQueue,
            VisitedCache visitedCache,
            boolean restrictToSameSubdomain,
            int maxConcurrentFetches // -1 for unlimited
    ) {
        this.pageFetcher = pageFetcher;
        this.urlQueue = urlQueue;
        this.visitedCache = visitedCache;
        this.restrictToSameSubdomain = restrictToSameSubdomain;
        this.executor = Executors.newVirtualThreadPerTaskExecutor();

        if (maxConcurrentFetches > 0) {
            this.concurrencyLimiter = new Semaphore(maxConcurrentFetches);
        } else {
            this.concurrencyLimiter = null;
        }
    }

    public void start() {
        if (!running.compareAndSet(false, true)) return;

        log.info("Starting WebCrawler...");
        while (running.get()) {
            try {
                Optional<CrawlUrl> maybeUrl = urlQueue.poll();
                if (maybeUrl.isEmpty()) continue;

                CrawlUrl crawlUrl = maybeUrl.get();

                if (visitedCache.isVisited(crawlUrl.url())) continue;

                executor.submit(() -> crawlUrlTask(crawlUrl));

            } catch (Exception e) {
                log.error("Error in main crawler loop", e);
            }
        }
    }

    private void crawlUrlTask(CrawlUrl crawlUrl) {
        try {
            if (concurrencyLimiter != null) {
                concurrencyLimiter.acquire();
            }

            String url = crawlUrl.url();
            if (visitedCache.isVisited(url)) return;

            visitedCache.markVisited(url);

            CrawlResult result = pageFetcher.fetch(url);
            log.info("Crawled URL: {} -> discovered {} links at {} [{}]",
                    url, result.discoveredUrls().size(), Instant.now(), Thread.currentThread().getName());

            for (String href : result.discoveredUrls()) {
                if (!visitedCache.isVisited(href)) {
                    if (restrictToSameSubdomain && !isSameSubdomain(url, href)) continue;
                    urlQueue.offer(new CrawlUrl(href, 0, Instant.now()));
                }
            }

        } catch (Exception e) {
            log.error("Error fetching page {}", crawlUrl.url(), e);
        } finally {
            if (concurrencyLimiter != null) {
                concurrencyLimiter.release();
            }
        }
    }

    public void stop() {
        running.set(false);
        executor.shutdown();
        try {
            if (!executor.awaitTermination(30, TimeUnit.SECONDS)) {
                log.warn("Executor did not terminate within the timeout.");
            }
        } catch (InterruptedException e) {
            log.error("Interrupted while shutting down crawler executor", e);
            Thread.currentThread().interrupt();
        }
    }

    private boolean isSameSubdomain(String baseUrl, String newUrl) {
        try {
            URI base = new URI(baseUrl);
            URI target = new URI(newUrl);
            return base.getHost().equalsIgnoreCase(target.getHost());
        } catch (Exception e) {
            log.warn("Failed to compare subdomains for {} and {}", baseUrl, newUrl, e);
            return false;
        }
    }

    public VisitedCache getVisitedCache() {
        if (!(visitedCache instanceof DemoLocalVisitedCache)) {
            throw new UnsupportedOperationException("VisitedCache is not an instance of DemoLocalVisitedCache");
        }
        return visitedCache;
    }
}