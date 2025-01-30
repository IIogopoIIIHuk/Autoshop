package com.autoshop.controller;

import com.autoshop.DTO.AutomobileDTO;
import com.autoshop.entity.Automobile;
import com.autoshop.entity.CarModel;
import com.autoshop.repo.AutomobileRepository;
import com.autoshop.repo.CarModelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/stats")
@RequiredArgsConstructor
public class StatsController {

    private final AutomobileRepository automobileRepository;
    private final CarModelRepository carModelRepository;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getStats() {
        List<Automobile> automobiles = automobileRepository.findAll();
        float totalIncome = automobiles.stream().map(Automobile::getIncomePrice).reduce(0f, Float::sum);

        Map<String, Object> response = new HashMap<>();
        response.put("automobiles", automobiles);
        response.put("income", totalIncome);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/reset")
    public ResponseEntity<String> resetStats() {
        automobileRepository.deleteAll();
        return ResponseEntity.ok("All automobile data has been reset.");
    }

    @GetMapping("/all")
    public ResponseEntity<Map<String, Object>> getAllStats() {
        List<Automobile> automobiles = automobileRepository.findAll();
        List<CarModel> models = carModelRepository.findAll();

        // Топ-5 автомобилей по доходу
        List<Automobile> top5ByIncome = automobiles.stream()
                .sorted(Comparator.comparing(Automobile::getIncomePrice).reversed())
                .limit(5)
                .collect(Collectors.toList());

        // Автомобили, отсортированные по количеству продаж
        List<Automobile> sortedByCount = automobiles.stream()
                .sorted(Comparator.comparing(Automobile::getCount).reversed())
                .collect(Collectors.toList());

        // Доходность по моделям
        Map<String, Float> modelIncomeMap = models.stream()
                .collect(Collectors.toMap(CarModel::getName, CarModel::getIncomePrice));

        Map<String, Object> response = new HashMap<>();
        response.put("top5ByIncome", top5ByIncome);
        response.put("sortedByCount", sortedByCount);
        response.put("modelIncome", modelIncomeMap);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/income")
    public ResponseEntity<List<Automobile>> getTop5Income() {
        List<Automobile> top5ByIncome = automobileRepository.findAll().stream()
                .sorted(Comparator.comparing(Automobile::getIncomePrice).reversed())
                .limit(5)
                .collect(Collectors.toList());

        return ResponseEntity.ok(top5ByIncome);
    }

    @GetMapping("/count")
    public ResponseEntity<List<Automobile>> getSortedByCount() {
        List<Automobile> sortedByCount = automobileRepository.findAll().stream()
                .sorted(Comparator.comparing(Automobile::getCount).reversed())
                .collect(Collectors.toList());

        return ResponseEntity.ok(sortedByCount);
    }

    @GetMapping("/model")
    public ResponseEntity<Map<String, Float>> getModelIncome() {
        Map<String, Float> modelIncomeMap = carModelRepository.findAll().stream()
                .collect(Collectors.toMap(CarModel::getName, CarModel::getIncomePrice));

        return ResponseEntity.ok(modelIncomeMap);
    }
}
