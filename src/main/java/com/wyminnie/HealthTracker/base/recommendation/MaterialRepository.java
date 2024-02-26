package com.wyminnie.healthtracker.base.recommendation;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MaterialRepository extends JpaRepository<Material, Long> {
    List<Material> findByType(String type);

    List<Material> findAll();
}
