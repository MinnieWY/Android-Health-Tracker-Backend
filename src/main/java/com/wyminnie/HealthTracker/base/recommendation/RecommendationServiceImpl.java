package com.wyminnie.healthtracker.base.recommendation;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class RecommendationServiceImpl implements RecommendationService {
    @Autowired
    private MaterialRepository materialRepository;

    @Override
    public List<MaterialListItemDTO> getRecommendedsMaterials(String preference) {
        return materialRepository.findByType(preference);
    }

    @Override
    public List<MaterialListItemDTO> getMaterialsList() {
        return materialRepository.findAllByIdDesc();
    }

    @Override
    public MaterialDTO getMaterialById(Long id) {
        Material selectedMaterial = materialRepository.findById(id).orElse(null);
        String materialType = selectedMaterial.getType();
        if (selectedMaterial == null) {
            return null;
        }
        MaterialDTO materialDTO = new MaterialDTO();
        materialDTO.setId(selectedMaterial.getId());
        materialDTO.setName(selectedMaterial.getName());
        materialDTO.setType(selectedMaterial.getType());

        switch (materialDTO.getType()) {
            case "article":
                materialDTO.setContent(selectedMaterial.getContent());
                break;
            case "video":
                materialDTO.setDescription(selectedMaterial.getShortDescription());
                materialDTO.setUrl(selectedMaterial.getUrl());
                break;
            case "soundtrack":
                materialDTO.setDescription(selectedMaterial.getShortDescription());
                materialDTO.setUrl(selectedMaterial.getUrl());
                break;
            default:
                break;
        }
        return materialDTO;
    }

}
