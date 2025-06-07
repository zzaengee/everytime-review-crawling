package com.datascience.everytime.service;

import com.datascience.everytime.model.KeywordEntry;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

import java.io.*;
import java.util.*;

public class KeywordExtractor {
    private static final String PYTHON = "python";
    private static final String SCRIPT_PATH = "src/main/resources/python/extract_keywords.py";

    public static List<KeywordEntry> extractTopKeywords(List<String> reviews) {
        try {
            ProcessBuilder pb = new ProcessBuilder(PYTHON, SCRIPT_PATH);
            Process process = pb.start();

            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonInput = objectMapper.writeValueAsString(reviews);
            writer.write(jsonInput);
            writer.flush();
            writer.close();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line);
            }

            int exitCode = process.waitFor();
            if (exitCode != 0) {
                throw new RuntimeException("Python process failed");
            }
            System.out.println("[DEBUG] KeywordExtractor: 전달된 리뷰 수 = " + reviews.size());
            System.out.println("[DEBUG] Python subprocess 실행 시작");;

            BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            String errLine;
            while ((errLine = errorReader.readLine()) != null) {
                System.err.println("[PYTHON STDERR] " + errLine);
            }

            return objectMapper.readValue(output.toString(), new TypeReference<List<KeywordEntry>>() {});
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
}