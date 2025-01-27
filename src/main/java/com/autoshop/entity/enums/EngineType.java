package com.autoshop.entity.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum EngineType {
    PETROL("Бензин"),
    HYBRID("Гибрид"),
    DIESEL("Дизель"),
    ;
    private final String name;
}

