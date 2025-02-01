package com.autoshop.controller;


import com.autoshop.DTO.CarModelDTO;
import com.autoshop.entity.CarModel;
import com.autoshop.repo.CarModelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/models")
@RequiredArgsConstructor
public class CarModelController {

    private final CarModelRepository carModelRepository;

    @PreAuthorize("permitAll()")
    @GetMapping
    public ResponseEntity<?> getAllModels(){
        List<CarModelDTO> models = carModelRepository.findAll().stream()
                .map(carModel -> new CarModelDTO(carModel.getId(), carModel.getName()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(models);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/add")
    public ResponseEntity<CarModelDTO> addModel(@RequestBody CarModelDTO carModelDTO){
        CarModel carModel = CarModel.builder()
                .name(carModelDTO.getName())
                .build();
        carModelRepository.save(carModel);
        return ResponseEntity.status(HttpStatus.CREATED).body(new CarModelDTO(carModel.getId(), carModel.getName()));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/edit")
    public ResponseEntity<CarModelDTO> updateModel(@PathVariable Long id, @RequestBody CarModelDTO carModelDTO) {
        CarModel carModel = carModelRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Модель не найдена"));

        carModel.setName(carModelDTO.getName());
        CarModel updatedModel = carModelRepository.save(carModel);
        return ResponseEntity.ok(new CarModelDTO(updatedModel.getId(), updatedModel.getName()));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}/delete")
    public ResponseEntity<Void> deleteModel(@PathVariable Long id) {
        if (!carModelRepository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        carModelRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
