// ReviewController.java
package com.datascience.everytime.controller;

import com.datascience.everytime.model.LectureProfessorPair;
import com.datascience.everytime.model.ReviewResult;
import com.datascience.everytime.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping("/review")
    public ReviewResult getReview(@RequestParam String lecture, @RequestParam String professor) throws Exception {
        return reviewService.getReviewResult(lecture, professor);
    }

    @GetMapping("/lectures")
    public List<LectureProfessorPair> getLectureProfessorPairs() throws Exception {
        return reviewService.getLectureProfessorPairs();
    }
}
