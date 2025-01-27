package com.autoshop.controller;

import com.autoshop.DTO.ApplicationDTO;
import com.autoshop.entity.Application;
import com.autoshop.entity.Automobile;
import com.autoshop.entity.CarModel;
import com.autoshop.entity.enums.ApplicationStatus;
import com.autoshop.repo.ApplicationRepository;
import com.autoshop.repo.AutomobileRepository;
import com.autoshop.repo.CarModelRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/automobiles")
public class AutomobileController {

    private final ApplicationRepository applicationRepository;
    private final AutomobileRepository automobileRepository;
    private final CarModelRepository carModelRepository;

    @GetMapping
    public ResponseEntity<?> automobiles(){
        String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
        List<Automobile> automobiles = automobileRepository.findAll();
        List<CarModel> carModels = carModelRepository.findAll();
        return ResponseEntity.ok("all automobiles is getting");
    }

    @GetMapping("/search")
    public ResponseEntity<?> search(@PathVariable Long carModelId){
        automobileRepository.findAllByCarModel_Id(carModelId);
        carModelRepository.findAll();
        return ResponseEntity.ok("search is get");
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> automobile(@PathVariable Long id){
        automobileRepository.getReferenceById(id);
        return ResponseEntity.ok("automobile is getting");
    }

    @PostMapping("/{id}/application")
    public ResponseEntity<?> application(@PathVariable Long id){
        String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
        log.info("Current user: {}", currentUser);

        Automobile automobile = automobileRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Automobile not found"));

        Application application = Application.builder()
                .id(automobile.getId())
                .price(automobile.getPrice())
                .buyer(currentUser)
                .titleAuto(automobile.getName())
                .status(ApplicationStatus.WAITING)
                .build();

        applicationRepository.save(application);
        return ResponseEntity.ok("ok");
    }

}
