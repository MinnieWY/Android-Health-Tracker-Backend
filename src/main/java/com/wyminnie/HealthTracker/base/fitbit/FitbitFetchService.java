package com.wyminnie.healthtracker.base.fitbit;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import jakarta.annotation.PostConstruct;
import reactor.core.publisher.Mono;

@Service
public class FitbitFetchService {

    private final WebClient webClient;
    private static final String fitbitApiBaseUrl = "https://api.fitbit.com/1/user/-/";

    public FitbitFetchService() {
        this.webClient = WebClient.builder()
                .baseUrl(fitbitApiBaseUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    public Mono<FitbitHRVInterval> getHeartRateVariabilitybyInterval(String accessToken, String argument) {
        return webClient.get()
                .uri(argument)
                .headers(headers -> headers.setBearerAuth(accessToken))
                .retrieve()
                .bodyToMono(FitbitHRVInterval.class);
    }

    public Mono<String> getStepsbyDate(String accessToken, String argument) {
        return webClient.get()
                .uri(argument)
                .headers(headers -> headers.setBearerAuth(accessToken))
                .retrieve()
                .bodyToMono(String.class);
    }

    public String getDataBySingleDateURL(String type, String date) {
        String argument = "";
        switch (type) {
            case "hrv":
                argument = "hrv/date/" + date + ".json";
                break;
            case "steps":
                argument = "activities/date/" + date + ".json";
                break;
            default:
                break;
        }
        return fitbitApiBaseUrl + argument;
    }

    public String getDataURLByWeek(String type, String dateStart, String dateEnd) {
        String argument = "";
        switch (type) {
            case "hrv":
                argument = "hrv/date/" + dateStart + "/" + dateEnd + ".json";
                break;
            default:
                break;
        }
        return fitbitApiBaseUrl + argument;
    }
}
