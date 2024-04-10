package com.wyminnie.healthtracker.base.stress;

import lombok.Data;
import lombok.Setter;

@Data
@Setter
public class StressTrendDTO {
    private Long userId;
    private Integer mean;
    private Integer mode;
    private String trend;
}
