package com.autoshop.controller;

import com.autoshop.DTO.AutomobileDTO;
import com.autoshop.entity.Automobile;
import com.autoshop.entity.CarModel;
import com.autoshop.repo.ApplicationRepository;
import com.autoshop.repo.AutomobileRepository;
import com.autoshop.repo.CarModelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/stats")
@RequiredArgsConstructor
public class StatsController {

    private final AutomobileRepository automobileRepository;
    private final CarModelRepository carModelRepository;
    private final ApplicationRepository applicationRepository;


    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping
    public ResponseEntity<Map<String, Object>> getStats() {
        List<Automobile> automobiles = automobileRepository.findAll();
        float totalIncome = automobiles.stream().map(Automobile::getIncomePrice).reduce(0f, Float::sum);

        List<Map<String, Object>> automobileStats = automobiles.stream().map(auto -> {
            Map<String, Object> autoMap = new HashMap<>();
            autoMap.put("id", auto.getId());
            autoMap.put("name", auto.getName());
            autoMap.put("photo", auto.getPhoto());
            autoMap.put("price", auto.getPrice());
            autoMap.put("origin", auto.getOrigin());
            autoMap.put("count", auto.getCount());
            autoMap.put("engineType", auto.getEngineType());
            autoMap.put("applications", auto.getApplications());
            autoMap.put("incomePrice", auto.getIncomePrice());
            autoMap.put("incomeCount", auto.getIncomeCount());

            if (auto.getCarModel() != null) {
                autoMap.put("model", auto.getCarModel().getName());
            }

            return autoMap;
        }).toList();

        Map<String, Object> response = new HashMap<>();
        response.put("income", totalIncome);
        response.put("automobiles", automobileStats);

        return ResponseEntity.ok(response);
    }


    // повесил админа, потому что удаляет все машины в базе,
    // хз зачем тут вообще этот метод, но было так, надо смотреть по дизайну,
    // остальные методы могут все юзать и админ и юзер
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/reset")
    public ResponseEntity<String> resetStats() {
        applicationRepository.deleteAll();
        return ResponseEntity.ok("All automobile data has been reset.");
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping("/all")
    public ResponseEntity<Map<String, Object>> getAllStats() {
        List<Automobile> automobiles = automobileRepository.findAll();
        List<CarModel> models = carModelRepository.findAll();

        // Топ-5 автомобилей по доходу
        List<Map<String, Object>> top5ByIncome = automobiles.stream()
                .sorted(Comparator.comparing(Automobile::getIncomePrice).reversed())
                .limit(5)
                .map(auto -> {
                    Map<String, Object> autoMap = new HashMap<>();
                    autoMap.put("id", auto.getId());
                    autoMap.put("name", auto.getName());
                    autoMap.put("price", auto.getPrice());
                    autoMap.put("incomePrice", auto.getIncomePrice());
                    autoMap.put("count", auto.getCount());
                    autoMap.put("photo", auto.getPhoto());
                    autoMap.put("origin", auto.getOrigin());
                    autoMap.put("engineType", auto.getEngineType());

                    if (auto.getCarModel() != null) {
                        autoMap.put("model", auto.getCarModel().getName());
                    }

                    return autoMap;
                }).toList();

        // Автомобили, отсортированные по количеству продаж
        List<Map<String, Object>> sortedByCount = automobiles.stream()
                .sorted(Comparator.comparing(Automobile::getCount).reversed())
                .map(auto -> {
                    Map<String, Object> autoMap = new HashMap<>();
                    autoMap.put("id", auto.getId());
                    autoMap.put("name", auto.getName());
                    autoMap.put("price", auto.getPrice());
                    autoMap.put("incomePrice", auto.getIncomePrice());
                    autoMap.put("count", auto.getCount());
                    autoMap.put("photo", auto.getPhoto());
                    autoMap.put("origin", auto.getOrigin());
                    autoMap.put("engineType", auto.getEngineType());

                    if (auto.getCarModel() != null) {
                        autoMap.put("model", auto.getCarModel().getName());
                    }

                    return autoMap;
                }).toList();

        // Доходность по моделям
        List<Map<String, Object>> modelIncomeList = carModelRepository.findAll().stream()
                .map(carModel -> {
                    Map<String, Object> modelMap = new LinkedHashMap<>();
                    modelMap.put("name", carModel.getName());
                    modelMap.put("price", carModel.getIncomePrice());
                    return modelMap;
                })
                .toList();

        Map<String, Object> response = new HashMap<>();
        response.put("top5ByIncome", top5ByIncome);
        response.put("sortedByCount", sortedByCount);
        response.put("modelIncome", modelIncomeList);

        return ResponseEntity.ok(response);
    }


    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping("/income")
    public ResponseEntity<List<Map<String, Object>>> getTop5Income() {
        List<Map<String, Object>> top5ByIncome = automobileRepository.findAll().stream()
                .sorted(Comparator.comparing(Automobile::getIncomePrice).reversed())
                .limit(5)
                .map(auto -> {
                    Map<String, Object> autoMap = new LinkedHashMap<>();
                    autoMap.put("id", auto.getId());
                    autoMap.put("name", auto.getName());
                    autoMap.put("price", auto.getPrice());
                    autoMap.put("incomePrice", auto.getIncomePrice());
                    autoMap.put("count", auto.getCount());
                    autoMap.put("photo", auto.getPhoto());
                    autoMap.put("origin", auto.getOrigin());
                    autoMap.put("engineType", auto.getEngineType());

                    if (auto.getCarModel() != null) {
                        autoMap.put("model", auto.getCarModel().getName());
                    }

                    return autoMap;
                }).toList();

        return ResponseEntity.ok(top5ByIncome);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping("/count")
    public ResponseEntity<List<Map<String, Object>>> getSortedByCount() {
        List<Map<String, Object>> sortedByCount = automobileRepository.findAll().stream()
                .sorted(Comparator.comparing(Automobile::getCount).reversed())
                .map(auto -> {
                    Map<String, Object> autoMap = new LinkedHashMap<>();
                    autoMap.put("id", auto.getId());
                    autoMap.put("name", auto.getName());
                    autoMap.put("price", auto.getPrice());
                    autoMap.put("incomePrice", auto.getIncomePrice());
                    autoMap.put("count", auto.getCount());
                    autoMap.put("photo", auto.getPhoto());
                    autoMap.put("origin", auto.getOrigin());
                    autoMap.put("engineType", auto.getEngineType());

                    if (auto.getCarModel() != null) {
                        autoMap.put("model", auto.getCarModel().getName());
                    }

                    return autoMap;
                }).toList();

        return ResponseEntity.ok(sortedByCount);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping("/model")
    public ResponseEntity<List<Map<String, Object>>> getModelIncome() {
        List<Map<String, Object>> modelIncomeList = carModelRepository.findAll().stream()
                .map(carModel -> {
                    Map<String, Object> modelMap = new LinkedHashMap<>();
                    modelMap.put("name", carModel.getName());
                    modelMap.put("price", carModel.getIncomePrice());
                    return modelMap;
                })
                .toList();

        return ResponseEntity.ok(modelIncomeList);
    }




}
