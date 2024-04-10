package com.wyminnie.healthtracker.base.stress;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;

import com.wyminnie.healthtracker.base.recommendation.MaterialListItemDTO;

import jakarta.persistence.criteria.CriteriaBuilder.In;

import static com.wyminnie.healthtracker.common.Utils.getPreviousWeekDate;

import java.time.LocalDate;
import java.util.Date;
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
    public StressDTO createStressRecord(long userId, String date, int level) throws StressRecordAlreadyExist {
        LocalDate today = LocalDate.now();
        if (stressRepository.findByUserIdAndDate(userId, today) != null) {
            throw new StressRecordAlreadyExist();
        }
        Stress stressRecord = new Stress();
        stressRecord.setUserId(userId);
        stressRecord.setDate(today);
        stressRecord.setStressLevel(level);
        stressRecord.setMonth(Integer.parseInt(date.split("-")[1]));
        stressRecord.setYear(Integer.parseInt(date.split("-")[0]));
        final Stress savedRecord = stressRepository.saveAndFlush(stressRecord);

        StressDTO savedStressDTO = new StressDTO();
        savedStressDTO.setId(savedRecord.getId());
        savedStressDTO.setStressLevel(savedRecord.getStressLevel());
        savedStressDTO.setDate(savedRecord.getDate().toString());
        return savedStressDTO;
    }

    @Override
    public StressTrendDTO getStresTrend(long userId) throws NoStressRecordException {
        LocalDate endDate = LocalDate.now().minusDays(1);
        LocalDate startDate = endDate.minusDays(13);

        List<Stress> stressRecords = stressRepository.findByUserIdAndDateBetween(userId, startDate, endDate);

        if (stressRecords.isEmpty()) {
            throw new NoStressRecordException();
        }

        int totalStressLevels = 0;
        Map<Integer, Integer> stressLevelCount = new HashMap<>();

        for (Stress stressRecord : stressRecords) {
            int stressLevel = stressRecord.getStressLevel();
            totalStressLevels += stressLevel;
            stressLevelCount.put(stressLevel, stressLevelCount.getOrDefault(stressLevel, 0) + 1);
        }

        int averageStressLevel = totalStressLevels / stressRecords.size();
        int modeStressLevel = getModeStressLevel(stressLevelCount);
        String trend = getStressTrend(stressRecords);

        StressTrendDTO stressTrendDTO = new StressTrendDTO();
        stressTrendDTO.setUserId(userId);
        stressTrendDTO.setMean(averageStressLevel);
        stressTrendDTO.setMode(modeStressLevel);
        stressTrendDTO.setTrend(trend);
        return stressTrendDTO;
    }

    private int getModeStressLevel(Map<Integer, Integer> stressLevelCount) {
        int modeStressLevel = 0;
        int maxCount = 0;

        for (Map.Entry<Integer, Integer> entry : stressLevelCount.entrySet()) {
            int stressLevel = entry.getKey();
            int count = entry.getValue();

            if (count > maxCount) {
                modeStressLevel = stressLevel;
                maxCount = count;
            }
        }

        return modeStressLevel;
    }

    private String getStressTrend(List<Stress> stressRecords) {
        int previousStressLevel = stressRecords.get(0).getStressLevel();
        boolean increasing = false;
        boolean decreasing = false;

        for (int i = 1; i < stressRecords.size(); i++) {
            int currentStressLevel = stressRecords.get(i).getStressLevel();

            if (currentStressLevel > previousStressLevel) {
                increasing = true;
            } else if (currentStressLevel < previousStressLevel) {
                decreasing = true;
            }

            previousStressLevel = currentStressLevel;
        }

        if (increasing && decreasing) {
            return "Fluctuating";
        } else if (increasing) {
            return "Increasing";
        } else if (decreasing) {
            return "Decreasing";
        } else {
            return "Constant";
        }
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
            stressDTO.setDate(stress.getDate().toString());
            return stressDTO;
        }).collect(Collectors.toList());
    }

    @Override
    public int getTodayStress(long userId) {
        LocalDate currentDate = LocalDate.now();
        Stress stress = stressRepository.findByUserIdAndDate(userId, currentDate);
        if (stress == null) {
            return 0;
        }
        return stress.getStressLevel();
    }

    @Override
    public StressDTO predictStressLevel(Long userId, String accessToken) throws MLFailedException {
        try {
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
                    Stress newStress = new Stress();
                    newStress.setUserId(userId);
                    newStress.setStressLevel(Integer.valueOf(stressLevel));
                    newStress.setDate(LocalDate.now());
                    final Stress savedRecord = stressRepository.saveAndFlush(newStress);

                    StressDTO savedStressDTO = new StressDTO();
                    savedStressDTO.setId(savedRecord.getId());
                    savedStressDTO.setStressLevel(savedRecord.getStressLevel());
                    savedStressDTO.setDate(savedRecord.getDate().toString());
                    return savedStressDTO;
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
                        throw new MLFailedException("UNKNOWN_ERROR_400");
                }
            } else {
                throw new MLFailedException("UNKNOWN_ERROR");
            }
        } catch (WebClientRequestException e) {
            Stress newStress = new Stress();
            newStress.setUserId(Long.valueOf(1));
            newStress.setStressLevel(5);
            newStress.setDate(LocalDate.now());
            stressRepository.saveAndFlush(newStress);

            throw new MLFailedException("CONNECT_FAILED");
        }
    }
}