package com.wyminnie.healthtracker.base.stress;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import com.wyminnie.healthtracker.base.recommendation.MaterialListItemDTO;

import jakarta.persistence.criteria.CriteriaBuilder.In;

import static com.wyminnie.healthtracker.common.Utils.getPreviousWeekDate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class StressServiceImpl implements StressService {

    @Autowired
    private StressRepository stressRepository;

    private final WebClient webClient;

    public StressServiceImpl(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://localhost:5000").build();
    }

    @Override
    public StressDTO createStressRecord(long userId, String date, int level) {
        if (stressRepository.findByUserIdAndDate(userId, date) != null) {
            return null;
        }
        Stress stressRecord = new Stress();
        stressRecord.setUserId(userId);
        stressRecord.setDate(date);
        stressRecord.setStressLevel(level);
        stressRecord.setMonth(Integer.parseInt(date.split("-")[1]));
        stressRecord.setYear(Integer.parseInt(date.split("-")[0]));
        final Stress savedRecord = stressRepository.saveAndFlush(stressRecord);

        StressDTO savedStressDTO = new StressDTO();
        savedStressDTO.setId(savedRecord.getId());
        savedStressDTO.setStressLevel(savedRecord.getStressLevel());
        savedStressDTO.setDate(savedRecord.getDate());
        return savedStressDTO;
    }

    @Override
    public Map<String, Integer> getPreviousWeekStress(long userId) {
        String startDate = getPreviousWeekDate();
        Map<String, Integer> stressRecord = new HashMap<>();
        for (int i = 0; i < 7; i++) {
            String dateToCheck = startDate + "-" + i;
            Stress stress = stressRepository.findByUserIdAndDate(userId, dateToCheck);
            if (stress != null) {
                stressRecord.put(dateToCheck, stress.getStressLevel());
            } else {
                stressRecord.put(dateToCheck, 0);
            }
        }
        return stressRecord;
    }

    @Override
    public int getDateStress(long userId, String date) {
        Stress stress = stressRepository.findByUserIdAndDate(userId, date);
        if (stress == null) {
            return 0;
        }
        return stress.getStressLevel();
    }

    @Override
    public List<StressDTO> getMonthStress(long userId, String month, String year) {
        Integer monthInt = Integer.parseInt(month);
        Integer yearInt = Integer.parseInt(year);

        List<Stress> stressList = stressRepository.findByUserIdAndMonthAndYearOrderByDate(userId, monthInt, yearInt);
        return stressList.stream().map(stress -> {
            StressDTO stressDTO = new StressDTO();
            stressDTO.setId(stress.getId());
            stressDTO.setStressLevel(stress.getStressLevel());
            stressDTO.setDate(stress.getDate());
            return stressDTO;
        }).collect(Collectors.toList());
    }

    @Override
    public int getTodayStress(long userId) {
        String currentDate = java.time.LocalDate.now().toString();
        Stress stress = stressRepository.findByUserIdAndDate(userId, currentDate);
        if (stress == null) {
            return 0;
        }
        return stress.getStressLevel();
    }

    @Override
    public int predictStressLevel(String accessToken) throws MLFailedException {
        BodyInserters.FormInserter<String> requestBody = BodyInserters
                .fromFormData("accessToken", accessToken);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        ResponseEntity<String> response = webClient.post()
                .uri("http://127.0.0.1:5000/predict")
                .headers(httpHeaders -> httpHeaders.addAll(headers))
                .body(requestBody)
                .retrieve()
                .toEntity(String.class)
                .block();

        if (response != null && response.getStatusCode().is2xxSuccessful()) {
            String stressLevel = response.getBody();
            if (stressLevel != null) {
                return Integer.valueOf(stressLevel);
            } else {
                throw new MLFailedException("Failed to retrieve stress level from the response");
            }
        } else if (response != null && response.getStatusCode().is4xxClientError()) {
            String errorResponse = response.getBody();
            switch (errorResponse) {
                case "MISS_ACCESS_TOKEN":
                    throw new MLFailedException("MISS_ACCESS_TOKEN");
                case "ABSENT_FEATURES":
                    throw new MLFailedException("ABSENT_FEATURES");
                case "Features is null":
                    throw new MLFailedException("ABSENT_FEATURES");
                default:
                    throw new MLFailedException("Unknown error occurred");
            }
        } else {
            throw new MLFailedException("Unknown error occurred");
        }
    }
}