package com.datascience.everytime.model;

import com.opencsv.bean.CsvBindByName;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ReviewEntry {

    @CsvBindByName(column = "lecture")
    private String lecture;

    @CsvBindByName(column = "professor")
    private String professor;

    @CsvBindByName(column = "star")
    private String star;

    @CsvBindByName(column = "review")
    private String review;

    @CsvBindByName(column = "sentiment")
    private String sentiment;
}