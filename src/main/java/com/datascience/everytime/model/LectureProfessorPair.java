package com.datascience.everytime.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LectureProfessorPair {
    private String lecture;
    private String professor;
}
