package com.datascience.everytime.service;

import com.datascience.everytime.model.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.opencsv.bean.CsvToBeanBuilder;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.*;

@Service
public class ReviewService {

    private static final Map<String, String> LECTURE_FILE_MAP = Map.of(
            "대영LS", "대영LS_reviews_labeled.csv",
            "대영RW", "대영RW_reviews_labeled.csv",
            "미네르바토의토론", "미네르바토의토론_reviews_labeled.csv",
            "미네르바읽기와쓰기", "미네르바읽기와쓰기_reviews_labeled.csv",
            "컴퓨터프로그래밍", "컴퓨터프로그래밍_reviews_labeled.csv",
            "컴퓨팅사고", "컴퓨팅사고_reviews_labeled.csv"
    );

    private final Map<String, List<ReviewEntry>> labeledCache = new HashMap<>();
    private final Map<String, List<ReviewEntry>> keywordCache = new HashMap<>();

    @PostConstruct
    public void init() throws IOException {
        for (String fileName : LECTURE_FILE_MAP.values()) {
            String labeledPath = "data/" + fileName;
            List<ReviewEntry> labeled = new CsvToBeanBuilder<ReviewEntry>(new FileReader(labeledPath))
                    .withType(ReviewEntry.class)
                    .withIgnoreLeadingWhiteSpace(true)
                    .build()
                    .parse();
            labeledCache.put(fileName, labeled);

            String keywordPath = "data/통합_" + fileName.replace("_reviews_labeled.csv", "_reviews.csv");
            List<ReviewEntry> keywords = new CsvToBeanBuilder<ReviewEntry>(new FileReader(keywordPath))
                    .withType(ReviewEntry.class)
                    .withIgnoreLeadingWhiteSpace(true)
                    .build()
                    .parse();
            keywordCache.put(fileName, keywords);
        }
        System.out.println("[INFO] CSV 캐싱 완료!");
    }

    public List<LectureProfessorPair> getLectureProfessorPairs() throws Exception {
        Set<LectureProfessorPair> pairSet = new HashSet<>();

        for (String fileName : LECTURE_FILE_MAP.values()) {
            List<ReviewEntry> entries = labeledCache.get(fileName);
            if (entries != null) {
                for (ReviewEntry entry : entries) {
                    pairSet.add(new LectureProfessorPair(
                            entry.getLecture().trim(),
                            entry.getProfessor().trim()
                    ));
                }
            }
        }

        return pairSet.stream()
                .sorted(Comparator.comparing(LectureProfessorPair::getLecture)
                        .thenComparing(LectureProfessorPair::getProfessor))
                .toList();
    }

    public ReviewResult getReviewResult(String lectureKey, String professor) throws Exception {
        String fileName = LECTURE_FILE_MAP.get(lectureKey);
        if (fileName == null) {
            throw new IllegalArgumentException("해당 강의에 대한 파일이 존재하지 않습니다: " + lectureKey);
        }

        List<ReviewEntry> allReviews = labeledCache.get(fileName);
        if (allReviews == null) throw new IllegalArgumentException("labeled cache 없음: " + fileName);

        List<ReviewEntry> filtered = allReviews.stream()
                .filter(r -> r.getProfessor().trim().equals(professor.trim()))
                .toList();

        if (filtered.isEmpty()) {
            return new ReviewResult("(데이터 없음)", professor, 0, 0, 0,
                    List.of(), List.of(), List.of(), List.of());
        }

        String actualLectureName = filtered.get(0).getLecture();

        long total = filtered.size();
        long pos = filtered.stream().filter(r -> r.getSentiment().equals("긍정")).count();
        long neg = filtered.stream().filter(r -> r.getSentiment().equals("부정")).count();
        long neu = filtered.stream().filter(r -> r.getSentiment().equals("중립")).count();

        List<ReviewSnippet> posReviews = filtered.stream()
                .filter(r -> r.getSentiment().equals("긍정"))
                .map(r -> new ReviewSnippet(Integer.parseInt(r.getStar()), r.getReview()))
                .limit(5)
                .toList();

        List<ReviewSnippet> negReviews = filtered.stream()
                .filter(r -> r.getSentiment().equals("부정"))
                .map(r -> new ReviewSnippet(Integer.parseInt(r.getStar()), r.getReview()))
                .limit(5)
                .toList();

        List<ReviewSnippet> neuReviews = filtered.stream()
                .filter(r -> r.getSentiment().equals("중립"))
                .map(r -> new ReviewSnippet(Integer.parseInt(r.getStar()), r.getReview()))
                .limit(5)
                .toList();

        List<ReviewEntry> keywordReviews = keywordCache.get(fileName);
        if (keywordReviews == null) throw new IllegalArgumentException("keyword cache 없음: " + fileName);

        List<String> keywordTexts = keywordReviews.stream()
                .filter(r -> r.getProfessor().trim().equals(professor.trim()))
                .map(ReviewEntry::getReview)
                .toList();

        List<KeywordEntry> topKeywords = KeywordExtractor.extractTopKeywords(keywordTexts);

        return new ReviewResult(actualLectureName, professor,
                (int) (pos * 100 / total),
                (int) (neg * 100 / total),
                (int) (neu * 100 / total),
                posReviews, negReviews, neuReviews,
                topKeywords);
    }
}
