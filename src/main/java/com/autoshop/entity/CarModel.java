package com.autoshop.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static com.autoshop.entity.Automobile.round;

@Data
@NoArgsConstructor
@Entity
public class CarModel implements Serializable {

    @Setter(AccessLevel.NONE)
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false, updatable = false)
    private Long id;

    private String name;

    @OneToMany(mappedBy = "carModel", cascade = CascadeType.ALL)
    private List<Automobile> automobiles = new ArrayList<>();

    public CarModel(String name) {
        this.name = name;
    }

    public float getIncomePrice() {
        return round(automobiles.stream().reduce(0f, (i, automobile) -> i + automobile.getIncomePrice(), Float::sum));
    }
}
