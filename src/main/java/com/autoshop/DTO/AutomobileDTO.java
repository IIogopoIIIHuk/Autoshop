package com.autoshop.DTO;

import com.autoshop.entity.CarModel;
import com.autoshop.entity.enums.EngineType;
import lombok.Data;

@Data
public class AutomobileDTO {
    private Long id;
    private String name;
    private String photo;
    private String origin;
    private Float price;
    private Integer count;
    private Long carModelId;
    private String carModelName;
    private EngineType engineType;

    public String getPhotoUrl() {
        if (photo != null && !photo.isEmpty()) {
            return "http://localhost:8080" + photo;
        }
        return null;
    }
}
