package com.benny.icalculation.application;

import com.benny.icalculation.application.exceptions.ConfigIncompleteException;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class FileDownloader {

    public static String getIcal() throws IOException, InterruptedException {
        return getIcal(Config.iCalUrl);
    }
    public static String getIcal(String url) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();

        if (url.isEmpty()) {
            throw new ConfigIncompleteException("URL not provided");
        }

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }
}
