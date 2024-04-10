package com.wyminnie.healthtracker.base;

import java.time.LocalDateTime;

import lombok.Data;
import lombok.Setter;

@Data
@Setter
public class SleepDTO {
    private String sleepDuration;
    private int sleepEfficiency;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}
