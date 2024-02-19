package com.wyminnie.healthtracker.base.stress;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashMap;
import java.util.Map;

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
        Stress stressRecord = new Stress();
        stressRecord.setUserId(userId);
        stressRecord.setDate(date);
        stressRecord.setStressLevel(level);

        stressRepository.save(stressRecord);
        return stressRepository.findByUserIdAndDate(userId, date);
    }

    @Override
    public Map<String, Integer> getWeeklyStress(long userId, String date) {
        Map<String, Integer> stressRecord = new HashMap<>();
        for (int i = 0; i < 7; i++) {
            String dateToCheck = date + "-" + i;
            StressDTO stress = stressRepository.findByUserIdAndDate(userId, dateToCheck);
            if (stress != null) {
                stressRecord.put(dateToCheck, stress.getStressLevel());
            } else {
                stressRecord.put(dateToCheck, 0);
            }
        }
        return stressRecord;
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