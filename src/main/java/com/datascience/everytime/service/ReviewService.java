
package com.datascience.everytime.service;

import com.datascience.everytime.model.LectureProfessorPair;
import com.datascience.everytime.model.ReviewEntry;
import com.datascience.everytime.model.ReviewResult;
import com.datascience.everytime.model.ReviewSnippet;
import com.datascience.everytime.model.KeywordEntry;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.bean.CsvToBeanBuilder;
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

    public List<LectureProfessorPair> getLectureProfessorPairs() throws Exception {
        Set<LectureProfessorPair> pairSet = new HashSet<>();

        for (String fileName : LECTURE_FILE_MAP.values()) {
            List<ReviewEntry> entries = new CsvToBeanBuilder<ReviewEntry>(new FileReader("data/" + fileName))
                    .withType(ReviewEntry.class)
                    .withIgnoreLeadingWhiteSpace(true)
                    .build()
                    .parse();

            for (ReviewEntry entry : entries) {
                pairSet.add(new LectureProfessorPair(
                        entry.getLecture().trim(),
                        entry.getProfessor().trim()
                ));
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

        List<ReviewEntry> allReviews = new CsvToBeanBuilder<ReviewEntry>(
                new FileReader("data/" + fileName))
                .withType(ReviewEntry.class)
                .withIgnoreLeadingWhiteSpace(true)
                .build()
                .parse();

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

        //double avgStar = filtered.stream()
            //.mapToInt(r -> Integer.parseInt(r.getStar()))
            //.average()
            //.orElse(0.0);

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

        List<String> reviewTexts = filtered.stream()
            .map(ReviewEntry::getReview)
            .toList();

        List<KeywordEntry> topKeywords = KeywordExtractor.extractTopKeywords(reviewTexts);

        return new ReviewResult(actualLectureName, professor,
                (int) (pos * 100 / total),
                (int) (neg * 100 / total),
                (int) (neu * 100 / total),
                posReviews, negReviews, neuReviews,
                topKeywords);  // ✅ 여기에 topKeywords 추가
            }
}