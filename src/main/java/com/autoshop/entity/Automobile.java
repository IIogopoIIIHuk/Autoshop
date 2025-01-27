package com.autoshop.entity;

import com.autoshop.entity.enums.ApplicationStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "automobile")
@Entity
public class Automobile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "auto_id", nullable = false, updatable = false)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "photo")
    private String photo;

    @Column(name = "price")
    private float price;

    @Column(name = "origin")
    private String origin;

    @Column(name = "count")
    private int count;


    @ManyToOne
    private CarModel carModel;

    @OneToMany(mappedBy = "automobile")
    private List<Application> applications = new ArrayList<>();


    public float getIncomePrice() {
        return round(applications.stream().reduce(0f, (i, application) -> {
            if (application.getStatus() == ApplicationStatus.DONE) return i + application.getPrice();
            return i;
        }, Float::sum));
    }

    public int getIncomeCount() {
        return applications.stream().reduce(0, (i, application) -> {
            if (application.getStatus() == ApplicationStatus.DONE) return i + 1;
            return i;
        }, Integer::sum);
    }

    public static float round(float value) {
        long factor = (long) Math.pow(10, 2);
        value = value * factor;
        long tmp = Math.round(value);
        return (float) tmp / factor;
    }
}
