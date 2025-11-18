package org.crawler.engine;

import org.crawler.model.CrawlResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class StreamingHtmlPageFetcherTest {

    private StreamingHtmlPageFetcher fetcher;
    private HttpClient mockClient;

    @BeforeEach
    void setUp() throws Exception {
        fetcher = new StreamingHtmlPageFetcher(1, 5000L);

        mockClient = mock(HttpClient.class);
        fetcher.clientPool.clear();
        fetcher.clientPool.add(mockClient);
    }

    private void mockHttpResponse(String htmlContent) throws Exception {
        HttpResponse<InputStream> mockResponse = mock(HttpResponse.class);
        InputStream stream = new ByteArrayInputStream(htmlContent.getBytes());
        when(mockResponse.body()).thenReturn(stream);
        when(mockClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class))).thenReturn(mockResponse);
    }

    @Test
    void testFetchBasicPage() throws Exception {
        String url = "https://example.com";
        String html = """
                <!DOCTYPE html>
                <html>
                <head>
                    <title>Example Page</title>
                    <meta name="description" content="This is an example">
                </head>
                <body>
                    <a href="/page1">Link 1</a>
                    <a href="https://example.com/page2">Link 2</a>
                </body>
                </html>
                """;
        mockHttpResponse(html);

        CrawlResult result = fetcher.fetch(url);

        assertEquals(url, result.url());
        assertEquals("Example Page", result.title());
        assertEquals("This is an example", result.description());
        assertTrue(result.discoveredUrls().contains("https://example.com/page1"));
        assertTrue(result.discoveredUrls().contains("https://example.com/page2"));
    }

    @Test
    void testFetchPageWithoutDescription() throws Exception {
        String html = """
                <!DOCTYPE html>
                <html>
                <head>
                    <title>No Description</title>
                </head>
                <body></body>
                </html>
                """;
        mockHttpResponse(html);

        CrawlResult result = fetcher.fetch("https://example.com");

        assertEquals("No Description", result.title());
        assertEquals("", result.description());
    }

    @Test
    void testFetchPageWithRelativeLinks() throws Exception {
        String html = """
                <!DOCTYPE html>
                <html>
                <head><title>Relative Links</title></head>
                <body>
                    <a href="subpage.html">Relative</a>
                    <a href="/absolute">Absolute</a>
                    <a href="../parent">Parent</a>
                    <a href="https://other.com">External</a>
                </body>
                </html>
                """;
        mockHttpResponse(html);

        CrawlResult result = fetcher.fetch("https://example.com/path/page.html");

        assertTrue(result.discoveredUrls().contains("https://example.com/path/subpage.html"));
        assertTrue(result.discoveredUrls().contains("https://example.com/absolute"));
        assertTrue(result.discoveredUrls().contains("https://example.com/parent"));
        assertTrue(result.discoveredUrls().contains("https://other.com"));
    }

    @Test
    void testFetchPageWithNoLinks() throws Exception {
        String html = "<html><head><title>No Links</title></head><body>Text only</body></html>";
        mockHttpResponse(html);

        CrawlResult result = fetcher.fetch("https://example.com");

        assertTrue(result.discoveredUrls().isEmpty());
    }

    @Test
    void testFetchEmptyPage() throws Exception {
        mockHttpResponse("");

        CrawlResult result = fetcher.fetch("https://example.com");

        assertEquals("", result.title());
        assertEquals("", result.description());
        assertTrue(result.discoveredUrls().isEmpty());
    }

    @Test
    void testInvalidUrl() {
        assertThrows(Exception.class, () -> fetcher.fetch("not a valid url"));
    }
}
