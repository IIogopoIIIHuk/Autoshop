package com.autoshop.repo;

import com.autoshop.entity.Application;
import com.autoshop.entity.Automobile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AutomobileRepository extends JpaRepository<Automobile, Long> {
    List<Automobile> findByName(String name);

    Automobile findByApplicationsContains(Application applications);
}
