package com.autoshop.DTO;

import com.autoshop.entity.enums.EngineType;
import lombok.Data;

@Data
public class ApplicationResponseDTO {

    private Long id;
    private Float price;
    private String status;
    private String titleAuto;
    private String buyer;

    private Long automobileId;
    private String automobilePhoto;
    private EngineType automobileEngineType;
    private String automobileOrigin;
    private String automobileModel;
    private Integer automobileCount;
}
