package org.crawler.engine;

import org.crawler.model.CrawlResult;
import org.crawler.model.PageFetcher;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class StreamingHtmlPageFetcher implements PageFetcher {

    private final Duration requestTimeout;
    private final List<HttpClient> clientPool;
    private final AtomicInteger nextClientIndex = new AtomicInteger(0);

    public StreamingHtmlPageFetcher(int clientPoolSize, Long timeout) {
        Duration connectTimeout = Duration.ofMillis(timeout);
        this.requestTimeout = Duration.ofMillis(timeout);

        this.clientPool = new ArrayList<>(clientPoolSize);
        for (int i = 0; i < clientPoolSize; i++) {
            HttpClient client = HttpClient.newBuilder()
                    .version(HttpClient.Version.HTTP_2)
                    .connectTimeout(connectTimeout)
                    .build();
            clientPool.add(client);
        }
    }

    private HttpClient getClient() {
        int index = Math.abs(nextClientIndex.getAndIncrement() % clientPool.size());
        return clientPool.get(index);
    }

    @Override
    public CrawlResult fetch(String urlString) throws Exception {
        HttpClient client = getClient();
        URI uri = URI.create(urlString);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .timeout(requestTimeout)
                .GET()
                .build();

        HttpResponse<InputStream> response = client.send(request, HttpResponse.BodyHandlers.ofInputStream());

        try (InputStream in = response.body()) {
            Document doc = Jsoup.parse(in, "UTF-8", urlString);

            String title = doc.title();
            String description = "";
            Element metaDesc = doc.selectFirst("meta[name=description]");
            if (metaDesc != null) {
                description = metaDesc.attr("content");
            }

            List<String> hrefs = new ArrayList<>();
            for (Element link : doc.select("a[href]")) {
                hrefs.add(link.absUrl("href"));
            }

            return CrawlResult.create(urlString, title, description, hrefs);
        }
    }
}
