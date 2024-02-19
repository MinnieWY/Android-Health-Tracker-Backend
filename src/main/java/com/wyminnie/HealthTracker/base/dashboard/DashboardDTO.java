package com.wyminnie.healthtracker.base.dashboard;

import java.util.Map;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DashboardDTO {
    private Map<String, Integer> hrv;
    private Map<String, Integer> steps;
}
