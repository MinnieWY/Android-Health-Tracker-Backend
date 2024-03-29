package com.wyminnie.healthtracker.base.dashboard;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wyminnie.healthtracker.base.fitbit.FitbitFetchService;
import com.wyminnie.healthtracker.base.fitbit.FitbitHRVInterval;
import com.wyminnie.healthtracker.base.user.User;

import reactor.core.publisher.Mono;
import static com.wyminnie.healthtracker.common.Utils.getPreviousDate;
import static com.wyminnie.healthtracker.common.Utils.getPreviousWeekDate;

@Service
public class DashboardServiceImpl implements DashboardService {
    @Autowired
    private FitbitFetchService fitbitFetchService;

    @Override
    public Map<String, Integer> getPreviousWeekHeartRateVariability(User user) {
        String argument = fitbitFetchService.getDataURLByWeek("hrv", getPreviousWeekDate(), getPreviousDate());

        Mono<FitbitHRVInterval> fitbitHRVIntervalMono = fitbitFetchService.getHeartRateVariabilitybyInterval(
                user.getAccessToken(),
                argument);
        FitbitHRVInterval fitbitHRVIntervalData = fitbitHRVIntervalMono.block();

        Map<String, Integer> hrvDataMap = new TreeMap<>();
        if (fitbitHRVIntervalData != null && fitbitHRVIntervalData.getHrv() != null
                && !fitbitHRVIntervalData.getHrv().isEmpty()) {
            List<FitbitHRVInterval.HRVData> hrvDataList = fitbitHRVIntervalData.getHrv();
            for (FitbitHRVInterval.HRVData hrvData : hrvDataList) {
                String date = hrvData.getDateTime();
                int sleepRmssd = (int) hrvData.getValue().getDeepRmssd();
                hrvDataMap.put(date, sleepRmssd);
            }
        }
        return hrvDataMap;
    }

    @Override
    public Map<String, Integer> getPreviousWeekStepsCount(User user)
            throws JsonProcessingException {
        String startDate = getPreviousWeekDate();
        String endDate = getPreviousDate();

        Map<String, Integer> stepsCountMap = new TreeMap<>();

        LocalDate currentDate = LocalDate.parse(startDate);
        LocalDate endDateInclusive = LocalDate.parse(endDate);

        while (!currentDate.isAfter(endDateInclusive)) {
            String dateString = currentDate.toString();

            String argument = fitbitFetchService.getDataBySingleDateURL("steps", dateString);
            Mono<String> fitbitStepsDataMono = fitbitFetchService.getStepsbyDate(user.getAccessToken(),
                    argument);
            String fitbitStepsData = fitbitStepsDataMono.block();

            if (fitbitStepsData != null) {
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode stepsDataNode = objectMapper.readTree(fitbitStepsData);
                int stepCount = stepsDataNode.path("summary").path("steps").asInt();
                stepsCountMap.put(dateString, stepCount);
            }

            currentDate = currentDate.plusDays(1);
        }

        return stepsCountMap;
    }

    @Override
    public BMIDTO getBMI(User user) throws ProfileNotCompleteException, BMIInvalidException {
        if (user.getWeight() == 0 || user.getHeight() == 0) {
            return null;
        }
        BMIDTO bmiDTO = new BMIDTO();
        double bmi = user.getWeight() / (user.getHeight() * user.getHeight());
        if (bmi < 10 || bmi > 50) {
            throw new BMIInvalidException();
        } else {
            bmiDTO.setBmi(bmi);
        }

        if (bmi < 18.5) {
            bmiDTO.setBmiCategory("Underweight");
        } else if (bmi >= 18.5 && bmi < 24.9) {
            bmiDTO.setBmiCategory("Normal");
        } else if (bmi >= 25 && bmi < 29.9) {
            bmiDTO.setBmiCategory("Overweight");
        } else {
            bmiDTO.setBmiCategory("Obese");
        }

        return bmiDTO;
    }
}
