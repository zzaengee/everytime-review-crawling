package com.datascience.everytime.service;

import com.datascience.everytime.model.KeywordEntry;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class KeywordExtractor {

    public static List<KeywordEntry> extractTopKeywords(String lectureKey, String professor) {
        try {
            URL url = new URL("https://python-keyword-server.onrender.com/keywords");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json; utf-8");
            conn.setDoOutput(true);
    
            // JSON body 작성
            ObjectMapper mapper = new ObjectMapper();
            String jsonInputString = mapper.writeValueAsString(Map.of(
                "lectureKey", lectureKey,
                "professor", professor
            ));
    
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }
    
            // 응답 읽기
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                response.append(line.trim());
            }
    
            return mapper.readValue(response.toString(), new TypeReference<List<KeywordEntry>>() {});
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
    // 요청용 내부 클래스
    private record KeywordRequest(String lectureKey, String professor) {}
}