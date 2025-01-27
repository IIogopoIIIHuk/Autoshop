package com.autoshop.entity;

import com.autoshop.entity.enums.ApplicationStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Data
@Builder
@Entity
public class Application implements Serializable {

    @Setter(AccessLevel.NONE)
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false, updatable = false)
    private Long id;

    private float price;

    @Enumerated(EnumType.STRING)
    private ApplicationStatus status = ApplicationStatus.WAITING;

    private String titleAuto;

    @ManyToOne
    private Automobile automobile;

    private String buyer;

    @ManyToOne
    private User owner;

    public Application(){};

    public Application(Automobile automobile, User owner) {
        this.price = automobile.getPrice();
        this.automobile = automobile;
        this.owner = owner;
    }

    public String toString(){
        return "Application{" +
                "id= " + id +
                ", price= " + price +
                ", status= " + status +
                ", automobile= " + automobile +
                ", buyer= " + buyer +
                ", owner= " + owner +
                '}';
    }
}
