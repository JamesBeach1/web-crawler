package org.crawler.engine.queue;

import org.crawler.model.CrawlResult;
import org.crawler.model.PageFetcher;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;


public class StreamingHtmlPageFetcher implements PageFetcher {

    private static final int TIMEOUT_MS = 10_000;

    @Override
    public CrawlResult fetch(String urlString) throws Exception {
        HttpURLConnection conn = (HttpURLConnection) new URI(urlString).toURL().openConnection();
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(TIMEOUT_MS);
        conn.setReadTimeout(TIMEOUT_MS);

        try (InputStream in = conn.getInputStream()) {
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

            return new CrawlResult(urlString, title, description, hrefs, Instant.now());
        }
    }
}
