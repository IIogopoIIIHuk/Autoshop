package com.autoshop.controller;
import com.autoshop.DTO.FeedbackResponseDTO;
import com.autoshop.entity.User;
import com.autoshop.repo.FeedbackRepository;
import com.autoshop.entity.Feedback;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/feedback")
@RequiredArgsConstructor
public class FeedbackController {

    private final FeedbackRepository feedbackRepository;

    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<List<Feedback>> getAllFeedback(){
        List<Feedback> feedbacks = feedbackRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
        return ResponseEntity.ok(feedbacks);
    }

    @PostMapping("/add")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<?> addFeedback(@RequestBody Feedback feedback) {
        String user = SecurityContextHolder.getContext().getAuthentication().getName();

        feedback.setOwner(user);

        Feedback savedFeedback = feedbackRepository.save(feedback);

        FeedbackResponseDTO responseDTO = new FeedbackResponseDTO();
        responseDTO.setId(savedFeedback.getId());
        responseDTO.setText(savedFeedback.getText());
        responseDTO.setDate(savedFeedback.getDate());
        responseDTO.setOwner(savedFeedback.getOwner());

        return ResponseEntity.ok(responseDTO);
    }
}
