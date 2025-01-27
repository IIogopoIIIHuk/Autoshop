package com.autoshop.service;

import com.autoshop.entity.Role;
import com.autoshop.repo.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class RoleService {
    private final RoleRepository roleRepository;

    public Role getUserRole(){
        return roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new NoSuchElementException("Role not found in database"));
    }
}
