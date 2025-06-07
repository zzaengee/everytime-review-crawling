package com.datascience.everytime.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ReviewSnippet {
    private int star;
    private String comment;
}
