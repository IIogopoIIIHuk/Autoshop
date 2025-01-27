package com.autoshop.controller;

import com.autoshop.entity.Application;
import com.autoshop.entity.Automobile;
import com.autoshop.entity.enums.ApplicationStatus;
import com.autoshop.repo.ApplicationRepository;
import com.autoshop.repo.AutomobileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/application")
public class ApplicationController {

    private final ApplicationRepository applicationRepository;
    private final AutomobileRepository automobileRepository;

    @GetMapping // http://localhost:8080/application
    public ResponseEntity<?> getApplication() {
        String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();

        List<String> roles = SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                .map(auth -> auth.getAuthority())
                .collect(Collectors.toList());

        List<Application> applications;

        if (roles.contains("ROLE_ADMIN")) {
            applications = applicationRepository.findAll();
        } else {
            applications = applicationRepository.findByBuyer(currentUser);
        }

        applications.sort(Comparator.comparing(Application::getId).reversed());

        return ResponseEntity.ok(applications);
    }

    @PostMapping("/{id}/done") // http://localhost:8080/application/1/done
    public ResponseEntity<?> markApplicationAsDone(@PathVariable Long id){
        Application application = applicationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Заявка не найдена"));

        application.setStatus(ApplicationStatus.DONE);
        applicationRepository.save(application);

        Automobile automobile = application.getAutomobile();
        if (automobile.getCount() > 0) {
            automobile.setCount(automobile.getCount() - 1);
            automobileRepository.save(automobile);
        } else {
            return ResponseEntity.badRequest().body("Недостаточно автомобилей для выполнения заявки");
        }

        return ResponseEntity.ok(application);
    }


    // PutMapping ?
    @PostMapping("/{id}/reject") // http://localhost:8080/application/1/reject
    public ResponseEntity<?> rejectApplication(@PathVariable Long id){
        Application application = applicationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Заявка не найдена"));

        application.setStatus(ApplicationStatus.REJECT);
        applicationRepository.save(application);

        return ResponseEntity.ok(application);
    }

    @DeleteMapping("/{id}/delete") // http://localhost:8080/application/1/delete
    public ResponseEntity<?> delete(@PathVariable Long id){
        if (!applicationRepository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Заявка не найдена");
        }

        applicationRepository.deleteById(id);
        return ResponseEntity.noContent().build(); // HTTP 204
    }

}
