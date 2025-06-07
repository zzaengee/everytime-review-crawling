package com.datascience.everytime.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;

@Data
@AllArgsConstructor
public class ReviewResult {
    private String lecture;
    private String professor;
    private int positiveRatio;
    private int negativeRatio;
    private int neutralRatio;
    private List<ReviewSnippet> positiveReviews;
    private List<ReviewSnippet> negativeReviews;
    private List<ReviewSnippet> neutralReviews;
    private List<KeywordEntry> topKeywords;
}