package com.wyminnie.healthtracker.base.stress;

import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Stress {
    @Id
    private Long id;
    private Long userId;
    private String date;
    private int stressLevel;
}
