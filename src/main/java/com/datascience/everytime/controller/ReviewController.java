// ReviewController.java
package com.datascience.everytime.controller;

import com.datascience.everytime.model.LectureProfessorPair;
import com.datascience.everytime.model.ReviewResult;
import com.datascience.everytime.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping("/review")
    public ResponseEntity<?> getReview(@RequestParam String lecture, @RequestParam String professor) {
        try {
            String decodedLecture = URLDecoder.decode(lecture, StandardCharsets.UTF_8);
            String decodedProfessor = URLDecoder.decode(professor, StandardCharsets.UTF_8);

            ReviewResult result = reviewService.getReviewResult(decodedLecture, decodedProfessor);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("에러 발생: " + e.getMessage());
        }
    }

    @GetMapping("/lectures")
    public List<LectureProfessorPair> getLectureProfessorPairs() throws Exception {
        return reviewService.getLectureProfessorPairs();
    }
}
