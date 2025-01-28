package com.autoshop.controller;

import com.autoshop.entity.Automobile;
import com.autoshop.repo.AutomobileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/about")
public class AboutController {

    private final AutomobileRepository automobileRepository;

    @GetMapping
    public List<Automobile> about(){
        return automobileRepository.findAll();
    }
}
