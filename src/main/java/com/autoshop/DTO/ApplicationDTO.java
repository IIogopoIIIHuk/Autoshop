package com.autoshop.DTO;

import com.autoshop.entity.enums.ApplicationStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ApplicationDTO {
    private Long id;
    private String buyer;
    private float price;
    private String titleAuto;
    private ApplicationStatus status = ApplicationStatus.WAITING;
    private AutomobileDTO automobile;
}
