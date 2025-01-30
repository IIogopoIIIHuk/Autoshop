package com.autoshop.controller;


import com.autoshop.DTO.UserDTO;
import com.autoshop.entity.Role;
import com.autoshop.entity.User;
import com.autoshop.repo.RoleRepository;
import com.autoshop.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;


    @GetMapping
    public ResponseEntity<?> getAllUsers(){
        List<User> users = userRepository.findAll();
        return ResponseEntity.ok(users);
    }


    @PutMapping("/editRole/{id}")
    public ResponseEntity<?> editRoleUser(@PathVariable Long id, @RequestParam String roleName){
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new RuntimeException("Роль не найдена"));

        user.setRoles(new ArrayList<>(List.of(role)));

        userRepository.save(user);

        return ResponseEntity.ok(new UserDTO(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getName(),
                user.getPhone()
        ));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id){
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        userRepository.delete(user);

        return ResponseEntity.ok(new UserDTO(
                user.getId(),
                user.getName(),
                user.getUsername(),
                user.getEmail(),
                user.getPhone()
        ));
    }

}
