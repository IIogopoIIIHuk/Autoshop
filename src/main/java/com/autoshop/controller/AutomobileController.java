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


    @GetMapping // URL: http://localhost:8080/automobiles
    public ResponseEntity<?> getAllAutomobiles(){
        List<Automobile> automobiles = automobileRepository.findAll();
        return ResponseEntity.ok("all automobiles is getting" + automobiles);
    }

    @GetMapping("/search") // http://localhost:8080/automobiles/search?carModelId=2
    public ResponseEntity<?> search(@PathVariable Long carModelId){
        List<Automobile> automobiles = automobileRepository.findAllByCarModel_Id(carModelId);
        return ResponseEntity.ok("search is get" + automobiles);
    }

    @GetMapping("/{id}") // http://localhost:8080/automobiles/1
    public ResponseEntity<?> getAutomobile(@PathVariable Long id){
        Automobile automobile = automobileRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Автомобиль не найден"));
        return ResponseEntity.ok("automobile is getting");
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
        return ResponseEntity.status(HttpStatus.CREATED).body(application);
    }


    @PostMapping("/add") // http://localhost:8080/automobiles/add
    public ResponseEntity<?> addAutomobile(@RequestBody AutomobileDTO automobileDTO,
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

//        CarModel carModel = carModelRepository.findById(carModelId)
//                .orElseThrow(() -> new RuntimeException("Модель автомобиля с ID " + carModelId + " не найдена"));

        Automobile automobile = Automobile.builder()
                .name(automobileDTO.getName())
                .photo(resultPhoto)
                .price(automobileDTO.getPrice())
                .origin(automobileDTO.getOrigin())
                .engineType(automobileDTO.getEngineType())
                .count(automobileDTO.getCount())
                .carModel(carModelRepository.getReferenceById(carModelId))
                .build();

        automobileRepository.save(automobile);
        return ResponseEntity.status(HttpStatus.CREATED).body(automobile);
    }

// фото выбрать
//    {
//        "name": "Tesla Model X",
//        "price": 60000,
//        "origin": "USA",
//        "count": 5,
//        "engineType": "ELECTRIC",
//        "carModelId": 2
//    }

    @PutMapping("/{id}/edit") // http://localhost:8080/automobiles/1/edit
    public ResponseEntity<?> editAutomobile(@PathVariable Long id,
                                            @RequestBody AutomobileDTO automobileDTO,
                                            @RequestBody MultipartFile photo,
                                            @RequestBody Long carModelId) {

        Automobile automobile = automobileRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Автомобиль не найден"));

        automobile = Automobile.builder()
                .name(automobileDTO.getName())
                .price(automobileDTO.getPrice())
                .origin(automobileDTO.getOrigin())
                .engineType(automobileDTO.getEngineType())
                .count(automobileDTO.getCount())
                .carModel(carModelRepository.getReferenceById(carModelId))
                .build();

        try {
            if (photo != null && !Objects.requireNonNull(photo.getOriginalFilename()).isEmpty()) {
                String uuidFile = UUID.randomUUID().toString();
                File uploadDir = new File(uploadImg);
                if (!uploadDir.exists()) uploadDir.mkdir();
                String resultPhoto = "automobile/" + uuidFile + "_" + photo.getOriginalFilename();
                photo.transferTo(new File(uploadImg + "/" + resultPhoto));
                automobile.setPhoto(resultPhoto);
            }
        } catch (IOException e) {
            return ResponseEntity.badRequest().body("Ошибка загрузки фотографии: " + e.getMessage());
        }

        automobile = automobileRepository.save(automobile);

        return ResponseEntity.ok(automobile);
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
