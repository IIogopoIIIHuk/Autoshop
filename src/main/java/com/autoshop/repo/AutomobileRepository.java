package com.autoshop.repo;

import com.autoshop.entity.Application;
import com.autoshop.entity.Automobile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AutomobileRepository extends JpaRepository<Automobile, Long> {
    @Query("SELECT a FROM Automobile a WHERE LOWER(a.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Automobile> findByNameContaining(@Param("name")String name);

    Automobile findByApplicationsContains(Application applications);
}
