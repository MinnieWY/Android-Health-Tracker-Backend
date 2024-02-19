package com.wyminnie.healthtracker.base.stress;

public interface StressService {
    StressDTO createStressRecord(long userId, String date, int level);

}