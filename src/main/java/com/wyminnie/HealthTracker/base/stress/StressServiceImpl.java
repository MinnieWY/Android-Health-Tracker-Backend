package com.wyminnie.healthtracker.base.stress;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StressServiceImpl implements StressService {
    @Autowired
    StressRepository stressRepository;

    @Override
    public StressDTO createStressRecord(long userId, String date, int level) {

        Stress stress_record = new Stress();
        stress_record.setUserId(userId);
        stress_record.setDate(date);
        stress_record.setStressLevel(level);

        stressRepository.save(stress_record);
        return stressRepository.findByUserIdAndDate(userId, date);
    }

}
