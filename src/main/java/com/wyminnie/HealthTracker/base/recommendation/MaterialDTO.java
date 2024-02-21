package com.wyminnie.healthtracker.base.recommendation;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MaterialDTO {
    private Long id;
    private String name;
    private String type;
    private String description;
    private String url;
    private String content;
}
