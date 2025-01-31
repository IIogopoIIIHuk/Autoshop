package com.autoshop.controller;

import com.autoshop.entity.Application;
import com.autoshop.entity.Automobile;
import com.autoshop.entity.User;
import com.autoshop.entity.enums.ApplicationStatus;
import com.autoshop.repo.ApplicationRepository;
import com.autoshop.repo.AutomobileRepository;
import com.autoshop.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
@Slf4j
@RequestMapping("/application")
public class ApplicationController {

    private final ApplicationRepository applicationRepository;
    private final AutomobileRepository automobileRepository;
    private final UserRepository userRepository;


    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping // http://localhost:8080/application
    public ResponseEntity<?> getApplication() {
        String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = userRepository.findByUsername(currentUser)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        List<String> roles = user.getRoles().stream()
                .map(role -> role.getName())
                .collect(Collectors.toList());

        List<Application> applications;

        log.info("Текущий пользователь: {}", currentUser);
        log.info("Роли пользователя: {}", roles);

        if (roles.contains("ROLE_ADMIN")) {
            applications = applicationRepository.findAll();
        } else if(roles.contains("ROLE_USER")) {
            applications = applicationRepository.findByBuyer(currentUser);
        } else
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Нет доступа к заявкам");

        applications.sort(Comparator.comparing(Application::getId).reversed());

        return ResponseEntity.ok(applications);
    }

    @PreAuthorize("hasRole('ADMIN')")
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
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}/reject") // http://localhost:8080/application/1/reject
    public ResponseEntity<?> rejectApplication(@PathVariable Long id){
        Application application = applicationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Заявка не найдена"));

        application.setStatus(ApplicationStatus.REJECT);
        applicationRepository.save(application);

        return ResponseEntity.ok(application);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}/delete") // http://localhost:8080/application/1/delete
    public ResponseEntity<?> delete(@PathVariable Long id){
        if (!applicationRepository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Заявка не найдена");
        }

        applicationRepository.deleteById(id);
        return ResponseEntity.noContent().build(); // HTTP 204
    }

}
