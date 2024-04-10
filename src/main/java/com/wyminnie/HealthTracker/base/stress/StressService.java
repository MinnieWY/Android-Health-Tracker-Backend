package com.wyminnie.healthtracker.base.stress;

import java.util.List;

public interface StressService {
    StressDTO createStressRecord(long userId, String date, int level) throws StressRecordAlreadyExist;

    int getTodayStress(long userId);

    StressTrendDTO getStresTrend(long userId) throws NoStressRecordException;

    List<StressDTO> getMonthStress(long userId, String month, String year);

    StressDTO predictStressLevel(Long userId, String accessToken) throws MLFailedException;

}