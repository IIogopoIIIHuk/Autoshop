package com.autoshop.controller;

import com.autoshop.DTO.ApplicationDTO;
import com.autoshop.DTO.AutomobileDTO;
import com.autoshop.entity.Application;
import com.autoshop.entity.Automobile;
import com.autoshop.entity.CarModel;
import com.autoshop.entity.enums.ApplicationStatus;
import com.autoshop.entity.enums.EngineType;
import com.autoshop.repo.ApplicationRepository;
import com.autoshop.repo.AutomobileRepository;
import com.autoshop.repo.CarModelRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/automobiles")
public class AutomobileController {

    private final ApplicationRepository applicationRepository;
    private final AutomobileRepository automobileRepository;
    private final CarModelRepository carModelRepository;

    @Value("${upload.img}")
    protected String uploadImg;

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

    @GetMapping("/add")
    public List<CarModel> add(){
        return carModelRepository.findAll();
    }

    @PostMapping("/add")
    public ResponseEntity<?> add(@RequestBody AutomobileDTO automobileDTO,
                                 @RequestParam MultipartFile photo,
                                 @RequestParam Long carModelId){
        String resultPhoto = "";
        try {
            if (photo != null && !Objects.requireNonNull(photo.getOriginalFilename()).isEmpty()) {
                String uuidFile = UUID.randomUUID().toString();
                File uploadDir = new File(uploadImg);
                if (!uploadDir.exists()) uploadDir.mkdir();
                resultPhoto = "automobile/" + uuidFile + "_" + photo.getOriginalFilename();
                photo.transferTo(new File(uploadImg + "/" + resultPhoto));
            }
        } catch (IOException e) {
            return ResponseEntity.badRequest().body("Ошибка загрузки фотографии: " + e.getMessage());
        }

        Automobile automobile = Automobile.builder()
                .name(automobileDTO.getName())
                .price(automobileDTO.getPrice())
                .origin(automobileDTO.getOrigin())
                .engineType(automobileDTO.getEngineType())
                .count(automobileDTO.getCount())
                .carModel(carModelRepository.getReferenceById(carModelId))
                .build();

        automobileRepository.save(automobile);
        return ResponseEntity.ok("auto is adding");
    }

}
