package com.wyminnie.healthtracker.base.recommendation;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class MaterialListItemDTO {
    private Long id;
    private String name;
    private String type;
    private String shortDescription;

    public static MaterialListItemDTO from(Material entity) {
        MaterialListItemDTO dto = new MaterialListItemDTO();
        dto.id = entity.getId();
        dto.name = entity.getName();
        dto.type = entity.getType();
        dto.shortDescription = entity.getShortDescription();

        return dto;
    }
}
