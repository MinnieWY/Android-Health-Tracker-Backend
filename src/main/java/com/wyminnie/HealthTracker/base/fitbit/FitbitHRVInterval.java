package com.wyminnie.healthtracker.base.fitbit;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class FitbitHRVInterval {
    @JsonProperty("hrv")
    private List<HRVData> hrv;

    public List<HRVData> getHrv() {
        return hrv;
    }

    public void setHrv(List<HRVData> hrv) {
        this.hrv = hrv;
    }

    public static class HRVData {
        @JsonProperty("value")
        private HRVValue value;

        @JsonProperty("dateTime")
        private String dateTime;

        public HRVValue getValue() {
            return value;
        }

        public void setValue(HRVValue value) {
            this.value = value;
        }

        public String getDateTime() {
            return dateTime;
        }

        public void setDateTime(String dateTime) {
            this.dateTime = dateTime;
        }
    }

    public static class HRVValue {
        @JsonProperty("dailyRmssd")
        private double dailyRmssd;

        @JsonProperty("deepRmssd")
        private double deepRmssd;

        public double getDailyRmssd() {
            return dailyRmssd;
        }

        public void setDailyRmssd(double dailyRmssd) {
            this.dailyRmssd = dailyRmssd;
        }

        public double getDeepRmssd() {
            return deepRmssd;
        }

        public void setDeepRmssd(double deepRmssd) {
            this.deepRmssd = deepRmssd;
        }
    }
}