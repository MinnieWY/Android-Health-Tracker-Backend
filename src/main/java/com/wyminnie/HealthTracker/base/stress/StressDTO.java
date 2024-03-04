package com.wyminnie.healthtracker.base.stress;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StressDTO {
    private Long id;
    private String date;
    private int stressLevel;

}