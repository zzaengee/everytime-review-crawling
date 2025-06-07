package com.datascience.everytime.service;

import com.datascience.everytime.model.KeywordEntry;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.util.Collections;
import java.util.List;

public class KeywordExtractor {

    private static final String PYTHON = "python";
    private static final String SCRIPT_PATH = "src/main/resources/python/extract_keywords.py";

    public static List<KeywordEntry> extractTopKeywords(List<String> reviews) {
        try {
            ProcessBuilder pb = new ProcessBuilder(PYTHON, SCRIPT_PATH);
            Process process = pb.start();

            // 자바에서 파이썬으로 리뷰 JSON 전달
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonInput = objectMapper.writeValueAsString(reviews);
            writer.write(jsonInput);
            writer.flush();
            writer.close();

            // 파이썬에서 출력한 결과 읽기
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line);
            }

            // 에러 로그 출력 (디버깅용)
            BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            String errLine;
            while ((errLine = errorReader.readLine()) != null) {
                System.err.println("[PYTHON STDERR] " + errLine);
            }

            int exitCode = process.waitFor();
            System.out.println("[DEBUG] Python process 종료 코드: " + exitCode);

            if (exitCode != 0) {
                throw new RuntimeException("Python process failed");
            }

            return objectMapper.readValue(output.toString(), new TypeReference<List<KeywordEntry>>() {});
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
}