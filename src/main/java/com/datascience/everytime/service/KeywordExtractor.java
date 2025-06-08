package com.datascience.everytime.service;

import com.datascience.everytime.model.KeywordEntry;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

public class KeywordExtractor {

    private static final String BASE_URL = "http://localhost:5050/keywords";

    public static List<KeywordEntry> extractTopKeywords(String lectureKey, String professor) {
        try {
            String encodedLecture = URLEncoder.encode(lectureKey, StandardCharsets.UTF_8);
            String encodedProfessor = URLEncoder.encode(professor, StandardCharsets.UTF_8);
            String urlWithParams = BASE_URL + "?lectureKey=" + encodedLecture + "&professor=" + encodedProfessor;

            URL url = new URL(urlWithParams);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            // 응답 읽기
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                response.append(line.trim());
            }
            br.close();

            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(response.toString(), new TypeReference<List<KeywordEntry>>() {});

        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
}