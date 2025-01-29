package com.autoshop.controller;

import ch.qos.logback.core.boolex.EvaluationException;
import com.autoshop.DTO.ApplicationDTO;
import com.autoshop.DTO.AutomobileDTO;
import com.autoshop.DTO.CarModelDTO;
import com.autoshop.entity.Application;
import com.autoshop.entity.Automobile;
import com.autoshop.entity.CarModel;
import com.autoshop.entity.enums.ApplicationStatus;
import com.autoshop.entity.enums.EngineType;
import com.autoshop.repo.ApplicationRepository;
import com.autoshop.repo.AutomobileRepository;
import com.autoshop.repo.CarModelRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
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


    @GetMapping // URL: http://localhost:8080/automobiles
    public ResponseEntity<?> getAllAutomobiles(){
        List<Automobile> automobiles = automobileRepository.findAll();
        return ResponseEntity.ok(automobiles);
    }

    @GetMapping("/searchAuto") // http://localhost:8080/automobiles/search?carModelId=2
    public ResponseEntity<?> searchByTitle(@RequestParam String name){
        List<Automobile> automobiles = automobileRepository.findByName(name);
        return ResponseEntity.ok(automobiles);
    }

    @GetMapping("/{id}") // http://localhost:8080/automobiles/1
    public ResponseEntity<?> getAutomobile(@PathVariable Long id){
        Automobile automobile = automobileRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Автомобиль не найден"));

        AutomobileDTO automobileDTO = new AutomobileDTO();
        automobileDTO.setId(automobile.getId());
        automobileDTO.setName(automobile.getName());
        automobileDTO.setPrice(automobile.getPrice());
        automobileDTO.setCount(automobile.getCount());
        automobileDTO.setOrigin(automobile.getOrigin());
        automobileDTO.setEngineType(automobile.getEngineType());
        automobileDTO.setPhoto(automobile.getPhoto());
        automobileDTO.setCarModel(automobile.getCarModel());

        return ResponseEntity.ok(automobileDTO);
    }

    @PostMapping("/{id}/application") // http://localhost:8080/automobiles/1/application
    public ResponseEntity<?> createApplication(@PathVariable Long id){
        String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
        log.info("Current user: {}", currentUser);

        Automobile automobile = automobileRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Automobile not found"));

        Application application = Application.builder()
                .price(automobile.getPrice())
                .buyer(currentUser)
                .titleAuto(automobile.getName())
                .status(ApplicationStatus.WAITING)
                .automobile(automobile)
                .build();

        applicationRepository.save(application);
        return ResponseEntity.ok(application);
    }

    @PostMapping(value = "/add", consumes = {"multipart/form-data"})
    public ResponseEntity<?> addAutomobile(
            @RequestPart(value = "auto") String autoJson,
            @RequestPart(value = "file", required = false) MultipartFile photo,
            @RequestParam(value = "carModelId") Long carModelId) {

        String resultPhoto = "";
        try {
            if (photo != null && !Objects.requireNonNull(photo.getOriginalFilename()).isEmpty()) {
                String uuidFile = UUID.randomUUID().toString();

                Path uploadPath = Paths.get(uploadImg, "automobile");
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                resultPhoto = "automobile/" + uuidFile + "_" + photo.getOriginalFilename();
                Path filePath = uploadPath.resolve(uuidFile + "_" + photo.getOriginalFilename());

                Files.copy(photo.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            }

            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            AutomobileDTO automobileDTO = objectMapper.readValue(autoJson, AutomobileDTO.class);

            Automobile automobile = Automobile.builder()
                    .name(automobileDTO.getName())
                    .photo(resultPhoto)
                    .price(automobileDTO.getPrice())
                    .origin(automobileDTO.getOrigin())
                    .engineType(automobileDTO.getEngineType())
                    .count(automobileDTO.getCount())
                    .carModel(carModelRepository.getReferenceById(carModelId))
                    .applications(new ArrayList<>())
                    .build();

            automobileRepository.save(automobile);
            return ResponseEntity.ok(automobile);

        } catch (IOException e) {
            return ResponseEntity.badRequest().body("Ошибка загрузки фотографии: " + e.getMessage());
        }
    }

//    {
//        "name": "Tesla Model X",
//        "price": 60000,
//        "origin": "USA",
//        "count": 5,
//        "engineType": "HYBRID",
//        "carModelId": 2
//    }

    @PutMapping("/{id}/edit") // http://localhost:8080/automobiles/1/edit
    public ResponseEntity<?> editAutomobile(
            @PathVariable Long id,
            @RequestPart(value = "auto") String autoJson,
            @RequestPart(value = "file", required = false) MultipartFile photo,
            @RequestParam(value = "carModelId") Long carModelId) {

        try {
            Automobile existingAuto = automobileRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Автомобиль не найден"));

            ObjectMapper objectMapper = new ObjectMapper();
            AutomobileDTO automobileDTO = objectMapper.readValue(autoJson, AutomobileDTO.class);

            String resultPhoto = existingAuto.getPhoto();
            if (photo != null && !photo.isEmpty()) {
                String uuidFile = UUID.randomUUID().toString();
                Path uploadPath = Paths.get(uploadImg, "automobile");
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }
                resultPhoto = "automobile/" + uuidFile + "_" + photo.getOriginalFilename();
                Path filePath = uploadPath.resolve(uuidFile + "_" + photo.getOriginalFilename());
                Files.copy(photo.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            }

            existingAuto.setName(automobileDTO.getName());
            existingAuto.setPrice(automobileDTO.getPrice());
            existingAuto.setOrigin(automobileDTO.getOrigin());
            existingAuto.setCount(automobileDTO.getCount());
            existingAuto.setEngineType(automobileDTO.getEngineType());
            existingAuto.setCarModel(carModelRepository.getReferenceById(carModelId));
            existingAuto.setPhoto(resultPhoto);

            automobileRepository.save(existingAuto);

            return ResponseEntity.ok(existingAuto);
        } catch (IOException e) {
            return ResponseEntity.badRequest().body("Ошибка обработки JSON: " + e.getMessage());
        }
    }


// фото выбрать
//    {
//        "name": "Tesla Model S Plaid",
//            "price": 70000,
//            "origin": "USA",
//            "count": 7,
//            "engineType": "ELECTRIC",
//            "carModelId": 2
//    }

    @DeleteMapping("/{id}/delete") // http://localhost:8080/automobiles/1/delete
    public ResponseEntity<?> delete(@PathVariable Long id){
        if (!automobileRepository.existsById(id)){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Автомобиль не найден");
        }
        automobileRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

}
