package com.autoshop.DTO;

import com.autoshop.entity.User;
import lombok.Data;

@Data
public class FeedbackResponseDTO {
    private Long id;
    private String date;
    private String text;
    private String owner;
}
