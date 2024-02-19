package com.wyminnie.healthtracker.base.stress;

import java.util.Map;

public interface StressService {
    StressDTO createStressRecord(long userId, String date, int level);

    Map<String, Integer> getWeeklyStress(long userId, String date);

}