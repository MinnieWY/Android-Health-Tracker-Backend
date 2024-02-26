package com.wyminnie.healthtracker.base.recommendation;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Material {
    @Id
    private Long id;
    private String name;
    private String type;
    private String url;
    private String shortDescription;
    private String content;
}
