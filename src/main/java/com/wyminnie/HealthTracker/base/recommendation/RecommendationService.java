package com.wyminnie.healthtracker.base.recommendation;

import java.util.List;

public interface RecommendationService {
    List<MaterialListItemDTO> getRecommendedsMaterials(String preference);

    List<MaterialListItemDTO> getMaterialsList();

    List<MaterialListItemDTO> getHightlighList();

    MaterialDTO getMaterialById(Long id);
}
