package com.wyminnie.healthtracker.base.recommendation;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MaterialRepository extends JpaRepository<Material, Long> {
    List<MaterialListItemDTO> findByType(String type);

    List<MaterialListItemDTO> findAllByIdDesc();
}
