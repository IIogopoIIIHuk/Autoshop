package com.autoshop.DTO;

import com.autoshop.entity.CarModel;
import lombok.Data;

@Data
public class AutomobileDTO {
    private String name;
    private String photo;
    private String origin;
    private float price;
    private int count;
    private CarModel carModel;
}
