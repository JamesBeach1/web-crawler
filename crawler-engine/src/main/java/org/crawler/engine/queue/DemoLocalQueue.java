package org.crawler.engine.queue;

import org.crawler.model.CrawlUrl;
import org.crawler.model.UrlQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class DemoLocalQueue implements UrlQueue {

    private static final Logger log = LoggerFactory.getLogger(DemoLocalQueue.class);

    private final LinkedBlockingQueue<CrawlUrl> queue;

    public DemoLocalQueue() {
        this.queue = new LinkedBlockingQueue<>();
    }

    public DemoLocalQueue(Set<CrawlUrl> seedUrls) {
        this.queue = new LinkedBlockingQueue<>();
        if (seedUrls != null) {
            queue.addAll(seedUrls);
        }
    }

    @Override
    public Optional<CrawlUrl> poll() {
        return Optional.ofNullable(queue.poll());
    }

    @Override
    public void offer(CrawlUrl url) {
        log.info("Offering URL to queue: {}", url.url());
        queue.offer(url);
    }
}
