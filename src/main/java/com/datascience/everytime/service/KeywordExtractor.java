package com.datascience.everytime.service;

import com.datascience.everytime.model.KeywordEntry;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

import java.io.*;
import java.util.*;

public class KeywordExtractor {

    private static final String PYTHON = "python";  // 시스템에 따라 python3일 수도 있음
    private static final String SCRIPT_PATH = "src/main/resources/python/extract_keywords.py";

    public static List<KeywordEntry> extractTopKeywords(List<String> reviews) {
        try {
            System.out.println("[DEBUG] KeywordExtractor: 리뷰 수 = " + reviews.size());

            ProcessBuilder pb = new ProcessBuilder(PYTHON, SCRIPT_PATH);
            Process process = pb.start();

            // 리뷰 리스트를 JSON으로 직렬화해서 파이썬으로 전달
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonInput = objectMapper.writeValueAsString(reviews);
            writer.write(jsonInput);
            writer.flush();
            writer.close();

            // 🔥 파이썬 stderr 먼저 읽어줌 (중요!)
            BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            String errLine;
            while ((errLine = errorReader.readLine()) != null) {
                System.err.println("[PYTHON STDERR] " + errLine);
            }

            // 파이썬 stdout 읽기
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line);
            }

            // 종료 코드 확인
            int exitCode = process.waitFor();
            System.out.println("[DEBUG] Python process 종료 코드: " + exitCode);
            if (exitCode != 0) {
                throw new RuntimeException("Python process failed");
            }

            // JSON 파싱해서 KeywordEntry 리스트로 반환
            return objectMapper.readValue(output.toString(), new TypeReference<List<KeywordEntry>>() {});

        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
}