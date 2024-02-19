package com.wyminnie.healthtracker.base.stress;

import java.util.HashMap;
import java.util.Map;

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

    @Override
    public Map<String, Integer> getWeeklyStress(long userId, String date) {
        Map<String, Integer> stress_record = new HashMap<>();
        for (int i = 0; i < 7; i++) {
            String dateToCheck = date + "-" + i;
            StressDTO stress = stressRepository.findByUserIdAndDate(userId, dateToCheck);
            if (stress != null) {
                stress_record.put(dateToCheck, stress.getStressLevel());
            } else {
                stress_record.put(dateToCheck, 0);
            }
        }

        return stress_record;
    }

}
