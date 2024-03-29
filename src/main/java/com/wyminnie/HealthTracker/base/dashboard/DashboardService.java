package com.wyminnie.healthtracker.base.dashboard;

import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.wyminnie.healthtracker.base.user.User;

public interface DashboardService {

    public Map<String, Integer> getPreviousWeekHeartRateVariability(User user);

    public Map<String, Integer> getPreviousWeekStepsCount(User user)
            throws JsonProcessingException;

    public BMIDTO getBMI(User user) throws ProfileNotCompleteException, BMIInvalidException;

}
