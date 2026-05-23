package com.example.benny.icalculation.core;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class FileDownloader {
    private FileDownloader() {}


    public static String getIcal() throws IOException, InterruptedException, URISyntaxException {
        return getIcal(Config.iCalUri.toURL());
    }
    public static String getIcal(URL url) throws IOException, InterruptedException, URISyntaxException {
        try (HttpClient client = HttpClient.newHttpClient()) {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(url.toURI())
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return response.body();
        }
    }
}
