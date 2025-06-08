package com.datascience.everytime.service;

import com.datascience.everytime.model.KeywordEntry;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Collections;
import java.util.List;

public class KeywordExtractor {

    private static final String FLASK_URL = "http://localhost:5000/keywords";  // Render에 올리면 여기를 실제 URL로 변경

    public static List<KeywordEntry> extractTopKeywords(List<String> reviews) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonInput = objectMapper.writeValueAsString(reviews);

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(FLASK_URL))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonInput))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new RuntimeException("Flask 서버 오류: " + response.statusCode());
            }

            return objectMapper.readValue(response.body(), new TypeReference<List<KeywordEntry>>() {});
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
}