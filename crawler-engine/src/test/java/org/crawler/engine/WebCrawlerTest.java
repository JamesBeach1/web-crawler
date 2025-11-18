package org.crawler.engine;

import org.crawler.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class WebCrawlerTest {

    private PageFetcher mockFetcher;
    private UrlQueue mockQueue;
    private VisitedCache mockCache;
    private WebCrawler crawler;

    @BeforeEach
    void setUp() {
        mockFetcher = mock(PageFetcher.class);
        mockQueue = mock(UrlQueue.class);
        mockCache = mock(VisitedCache.class);

        crawler = new WebCrawler(mockFetcher, mockQueue, mockCache, false, 2);
    }

    @Test
    void testCrawlSingleUrl() throws Exception {
        String url = "https://example.com";

        CrawlResult result = CrawlResult.create(url, "Title", "Description", List.of("https://example.com/page1", "https://other.com"));

        when(mockQueue.poll()).thenReturn(Optional.of(CrawlUrl.create(url, 0))).thenReturn(Optional.empty());
        when(mockCache.isVisited(anyString())).thenReturn(false);
        when(mockFetcher.fetch(url)).thenReturn(result);

        Thread crawlerThread = new Thread(crawler::start);
        crawlerThread.start();

        Thread.sleep(500);

        crawler.stop();
        crawlerThread.join();

        verify(mockFetcher, times(1)).fetch(url);

        ArgumentCaptor<CrawlUrl> captor = ArgumentCaptor.forClass(CrawlUrl.class);
        verify(mockQueue, atLeastOnce()).offer(captor.capture());
        List<CrawlUrl> offeredUrls = captor.getAllValues();
        assertTrue(offeredUrls.stream().anyMatch(u -> u.url().equals("https://example.com/page1")));
        assertTrue(offeredUrls.stream().anyMatch(u -> u.url().equals("https://other.com")));

        verify(mockCache).markVisited(url, 0);
    }

    @Test
    void testCrawlRespectsVisitedCache() throws Exception {
        String url = "https://example.com";
        when(mockQueue.poll()).thenReturn(Optional.of(CrawlUrl.create(url, 0))).thenReturn(Optional.empty());
        when(mockCache.isVisited(url)).thenReturn(true); // Already visited

        Thread crawlerThread = new Thread(crawler::start);
        crawlerThread.start();
        Thread.sleep(200);
        crawler.stop();
        crawlerThread.join();

        verify(mockFetcher, never()).fetch(anyString());
    }

    @Test
    void testCrawlerStopsGracefully() throws Exception {
        when(mockQueue.poll()).thenReturn(Optional.empty());

        Thread crawlerThread = new Thread(crawler::start);
        crawlerThread.start();
        Thread.sleep(200);

        crawler.stop();
        crawlerThread.join();

        assertFalse(crawler.running.get());
    }

    @Test
    void testRestrictToSameSubdomain() throws Exception {
        crawler = new WebCrawler(mockFetcher, mockQueue, mockCache, true, 2);

        String seedUrl = "https://example.com";
        CrawlResult result = CrawlResult.create(seedUrl, "Title", "Desc", List.of("https://example.com/page1", "https://other.com"));

        when(mockQueue.poll()).thenReturn(Optional.of(CrawlUrl.create(seedUrl, 0))).thenReturn(Optional.empty());
        when(mockCache.isVisited(anyString())).thenReturn(false);
        when(mockFetcher.fetch(seedUrl)).thenReturn(result);

        Thread crawlerThread = new Thread(crawler::start);
        crawlerThread.start();
        Thread.sleep(500);
        crawler.stop();
        crawlerThread.join();

        ArgumentCaptor<CrawlUrl> captor = ArgumentCaptor.forClass(CrawlUrl.class);
        verify(mockQueue, atLeastOnce()).offer(captor.capture());

        List<CrawlUrl> offeredUrls = captor.getAllValues();
        assertTrue(offeredUrls.stream().anyMatch(u -> u.url().equals("https://example.com/page1")));
        assertFalse(offeredUrls.stream().anyMatch(u -> u.url().equals("https://other.com")));
    }

    @Test
    void testGetVisitedCacheThrowsIfWrongType() {
        VisitedCache nonDemoCache = mock(VisitedCache.class);
        WebCrawler wc = new WebCrawler(mockFetcher, mockQueue, nonDemoCache, false, 1);

        assertThrows(UnsupportedOperationException.class, wc::getVisitedCache);
    }
}
