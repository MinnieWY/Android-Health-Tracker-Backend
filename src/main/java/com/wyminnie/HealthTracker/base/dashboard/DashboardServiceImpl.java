package com.wyminnie.healthtracker.base.dashboard;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wyminnie.healthtracker.base.fitbit.FitbitFetchService;
import com.wyminnie.healthtracker.base.fitbit.FitbitHRVInterval;
import com.wyminnie.healthtracker.base.fitbit.FitbitOAuthService;
import com.wyminnie.healthtracker.base.fitbit.FitbitHRVInterval.HRVData;
import com.wyminnie.healthtracker.base.user.User;

import reactor.core.publisher.Mono;

@Service
public class DashboardServiceImpl implements DashboardService {
    @Autowired
    private FitbitFetchService fitbitFetchService;

    private String getPreviousDate() {
        LocalDate currentDate = LocalDate.now();
        return currentDate.minusDays(1).toString();
    }

    private String getPreviousWeekDate() {
        LocalDate currentDate = LocalDate.now();
        return currentDate.minusDays(7).toString();
    }

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
            throws JsonMappingException, JsonProcessingException {
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

}
