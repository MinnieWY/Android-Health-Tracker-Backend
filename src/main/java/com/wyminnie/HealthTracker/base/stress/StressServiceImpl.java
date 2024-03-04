package com.wyminnie.healthtracker.base.stress;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import com.wyminnie.healthtracker.base.recommendation.MaterialListItemDTO;

import static com.wyminnie.healthtracker.common.Utils.getPreviousWeekDate;

import java.util.HashMap;
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
    public int getTodayStress(long userId) {
        String currentDate = java.time.LocalDate.now().toString();
        Stress stress = stressRepository.findByUserIdAndDate(userId, currentDate);
        if (stress == null) {
            return 0;
        }
        return stress.getStressLevel();
    }

    @Override
    public int predictStressLevel(String accessToken) {
        BodyInserters.FormInserter<String> requestBody = BodyInserters
                .fromFormData("accessToken", accessToken);

        // Set the request headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        return webClient.post()
                .uri("/predict")
                .headers(httpHeaders -> httpHeaders.addAll(headers))
                .body(requestBody)
                .retrieve()
                .bodyToMono(Integer.class)
                .block();
    }
}