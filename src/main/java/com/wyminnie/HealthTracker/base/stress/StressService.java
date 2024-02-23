package com.wyminnie.healthtracker.base.stress;

import java.util.Map;

public interface StressService {
    StressDTO createStressRecord(long userId, String date, int level);

    int getTodayStress(long userId);

    Map<String, Integer> getWeeklyStress(long userId, String date);

    int predictStressLevel(String accessToken);

}