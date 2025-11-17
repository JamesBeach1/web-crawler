package org.crawler.engine.queue;

import org.crawler.model.CrawlUrl;
import org.crawler.model.UrlQueue;

import java.util.Optional;
import java.util.concurrent.ConcurrentLinkedQueue;

public class DemoLocalQueue implements UrlQueue {

    private final ConcurrentLinkedQueue<CrawlUrl> queue = new ConcurrentLinkedQueue<>();

    @Override
    public Optional<CrawlUrl> poll() {
        return Optional.ofNullable(queue.poll());
    }

    @Override
    public void offer(CrawlUrl url) {
        queue.offer(url);
    }
}
