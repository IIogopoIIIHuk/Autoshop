package com.autoshop.controller;

import com.autoshop.entity.Application;
import com.autoshop.entity.Automobile;
import com.autoshop.entity.enums.ApplicationStatus;
import com.autoshop.repo.ApplicationRepository;
import com.autoshop.repo.AutomobileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/application")
public class ApplicationController {

    private final ApplicationRepository applicationRepository;
    private final AutomobileRepository automobileRepository;

    @GetMapping // если роль админа, то выводить весь список, если роль юзера то только его
    public ResponseEntity<?> application(){
        String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
        List<Application> applications = applicationRepository.findByBuyerName(currentUser);

        applications.sort(Comparator.comparing(Application::getId));
        Collections.reverse(applications);
        return ResponseEntity.ok("Getting user applications");
    }

    @PostMapping("/{id}/done")
    public ResponseEntity<?> done(@PathVariable Long id){
        Application application = applicationRepository.getReferenceById(id);
        application.setStatus(ApplicationStatus.DONE);
        applicationRepository.save(application);
        Automobile automobile = automobileRepository.findByApplication(application);
        automobile.setCount(automobile.getCount() - 1);
        automobileRepository.save(automobile);
        return ResponseEntity.ok("automobile is done");
    }


    // PutMapping ?
    @PostMapping("/{id}/reject")
    public ResponseEntity<?> reject(@PathVariable Long id){
        Application application = applicationRepository.getReferenceById(id);
        application.setStatus(ApplicationStatus.REJECT);
        applicationRepository.save(application);
        return ResponseEntity.ok("application is reject");
    }

    @DeleteMapping("/{id}/delete")
    public ResponseEntity<?> delete(@PathVariable Long id){
        Application application = applicationRepository.getReferenceById(id);
        applicationRepository.deleteById(id);
        return ResponseEntity.ok("application is deleting");
    }

}
