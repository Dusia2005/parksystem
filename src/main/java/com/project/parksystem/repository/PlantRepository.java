package com.project.parksystem.repository;

import com.project.parksystem.model.Plant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PlantRepository extends JpaRepository<Plant, Long> {
    Optional<Plant> findByNameIgnoreCase(String name);
    boolean existsByNameIgnoreCase(String name);
}
