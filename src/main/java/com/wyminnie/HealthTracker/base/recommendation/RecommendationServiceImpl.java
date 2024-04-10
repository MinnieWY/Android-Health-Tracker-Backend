package com.wyminnie.healthtracker.base.recommendation;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RecommendationServiceImpl implements RecommendationService {
    @Autowired
    private MaterialRepository materialRepository;

    @Override
    public List<MaterialListItemDTO> getRecommendedsMaterials(String preference) {
        return materialRepository.findByType(preference).stream().map(MaterialListItemDTO::from)
                .collect(Collectors.toList());
    }

    @Override
    public List<MaterialListItemDTO> getMaterialsList() {
        return materialRepository.findAll().stream().map(MaterialListItemDTO::from).collect(Collectors.toList());
    }

    @Override
    public MaterialDTO getMaterialById(Long id) {
        Material selectedMaterial = materialRepository.findById(id).orElse(null);
        if (selectedMaterial == null) {
            return null;
        }

        MaterialDTO materialDTO = new MaterialDTO();
        materialDTO.setId(selectedMaterial.getId());
        materialDTO.setName(selectedMaterial.getName());
        materialDTO.setType(selectedMaterial.getType());
        materialDTO.setContent(selectedMaterial.getContent());
        materialDTO.setUrl(selectedMaterial.getUrl());
        materialDTO.setDescription(selectedMaterial.getShortDescription());

        return materialDTO;

    }

    @Override
    public List<MaterialListItemDTO> getHightlighList() {
        return materialRepository.findAll().stream().map(MaterialListItemDTO::from).collect(Collectors.toList());
    }

}
