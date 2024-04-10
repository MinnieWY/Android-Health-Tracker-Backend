package com.wyminnie.healthtracker.common;

import java.time.LocalDate;

public class Utils {

    public static String getPreviousDate() {
        LocalDate currentDate = LocalDate.now();
        return currentDate.minusDays(1).toString();
    }

    public static String getPreviousWeekDate() {
        LocalDate currentDate = LocalDate.now();
        return currentDate.minusDays(7).toString();
    }

    public static String getToday() {
        return LocalDate.now().toString();
    }
}
