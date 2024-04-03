package com.wyminnie.healthtracker.base.stress;

import java.util.List;
import java.util.Map;

public interface StressService {
    StressDTO createStressRecord(long userId, String date, int level);

    int getTodayStress(long userId);

    int getDateStress(long userId, String date);

    Map<String, Integer> getPreviousWeekStress(long userId);

    List<StressDTO> getMonthStress(long userId, String month, String year);

    int predictStressLevel(String accessToken) throws MLFailedException;

}