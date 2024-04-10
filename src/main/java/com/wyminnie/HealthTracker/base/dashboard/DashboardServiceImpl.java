package com.wyminnie.healthtracker.base.dashboard;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.NumberFormat;
import java.io.IOException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wyminnie.healthtracker.base.SleepDTO;
import com.wyminnie.healthtracker.base.fitbit.FitbitFetchService;
import com.wyminnie.healthtracker.base.fitbit.FitbitHRVInterval;
import com.wyminnie.healthtracker.base.user.User;

import reactor.core.publisher.Mono;
import static com.wyminnie.healthtracker.common.Utils.getPreviousDate;
import static com.wyminnie.healthtracker.common.Utils.getPreviousWeekDate;
import static com.wyminnie.healthtracker.common.Utils.getToday;

@Service
public class DashboardServiceImpl implements DashboardService {
    @Autowired
    private FitbitFetchService fitbitFetchService;

    private static final double[][] bmiDistribution = {
            { 5.7, 42.9, 20.8, 37.5 }, // Female
            { 14.4, 51, 15.9, 15.5 } // Male
    };

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
        double denominator = (user.getHeight() / 100) * (user.getHeight() / 100);

        double bmi = Math.round((user.getWeight() / denominator) * 10.0) / 10.0;
        if (bmi < 10 || bmi > 50) {
            throw new BMIInvalidException();
        } else {
            bmiDTO.setBmi(bmi);
        }

        String category = getBMICategory(bmi);
        bmiDTO.setBmiCategory(category);

        int ranking = calculateBMIRanking(user.getGender(), category);
        bmiDTO.setBmiRanking(ranking);

        return bmiDTO;
    }

    @Override
    public byte[] getSharing(String username, Integer steps, String date) throws IOException {

        BufferedImage backgroundImage = loadBackgroundImage(
                "src/main/java/com/wyminnie/healthtracker/base/dashboard/walk_template.jpg");

        // Create a blank image with the same dimensions as the background image
        BufferedImage imageWithText = new BufferedImage(
                backgroundImage.getWidth(),
                backgroundImage.getHeight(),
                BufferedImage.TYPE_INT_RGB);

        // Get the graphics object of the new image
        Graphics2D g2d = imageWithText.createGraphics();

        // Draw the background image onto the new image
        g2d.drawImage(backgroundImage, 0, 0, null);

        // Set the text font, color, and size
        Font font_number = new Font("Arial", Font.BOLD, 100);
        g2d.setFont(font_number);
        g2d.setColor(Color.BLACK);

        String formattedSteps = NumberFormat.getNumberInstance(Locale.US).format(steps);

        String text_number = formattedSteps + " steps";
        int x_number = 250;
        int y_number = 700;
        g2d.drawString(text_number, x_number, y_number);

        Font font_text = new Font("Arial", Font.BOLD, 75);
        g2d.setFont(font_text);
        g2d.setColor(Color.BLACK);

        String text_text_days = "for a week";
        int x_text_days = 340;
        int y_text_days = 830;
        g2d.drawString(text_text_days, x_text_days, y_text_days);

        String text_text_date = "since " + date;
        int x_text_date = 250;
        int y_text_date = 950;
        g2d.drawString(text_text_date, x_text_date, y_text_date);

        Font font_name = new Font("Arial", Font.ITALIC, 60);
        g2d.setFont(font_name);
        g2d.setColor(Color.BLACK);

        String text_name = "@" + username;
        int x_name = 400;
        int y_name = 1100;
        g2d.drawString(text_name, x_name, y_name);

        // Dispose the graphics object
        g2d.dispose();

        // Convert the image to byte array
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(imageWithText, "jpg", baos);

        return baos.toByteArray();
    }

    private static BufferedImage loadBackgroundImage(String imagePath) throws IOException {
        File file = new File(imagePath);
        return ImageIO.read(file);
    }

    public int calculateBMIRanking(String sex, String category) {
        int sexIndex = sex.equalsIgnoreCase("female") ? 0 : 1;

        int categoryIndex = -1;
        switch (category) {
            case "Underweight":
                categoryIndex = 0;
                break;
            case "Normal":
                categoryIndex = 1;
                break;
            case "Overweight":
                categoryIndex = 2;
                break;
            case "Obese":
                categoryIndex = 3;
                break;
            default:
                break;
        }

        double[] categoryDistribution = bmiDistribution[sexIndex];

        int ranking = 0;
        for (int i = 0; i < categoryIndex; i++) {
            ranking += categoryDistribution[i];
        }

        return 100 - ranking;
    }

    private String getBMICategory(double bmiValue) {
        if (bmiValue < 18.5) {
            return "Underweight";
        } else if (bmiValue < 23) {
            return "Normal";
        } else if (bmiValue < 25) {
            return "Overweight";
        } else {
            return "Obese";
        }

    }

    @Override
    public SleepDTO getSleepData(User user) throws SleepDataAbsentException, FitbitFailException {

        SleepDTO sleepDTO = new SleepDTO();

        try {
            String argument = fitbitFetchService.getDataBySingleDateURL("sleep", getToday());

            Mono<String> fitbitSleepDataMono = fitbitFetchService.getStepsbyDate(user.getAccessToken(),
                    argument);

            String fitbitSleepData = fitbitSleepDataMono.block();

            if (fitbitSleepData != null) {
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode sleepDataNode = objectMapper.readTree(fitbitSleepData);
                JsonNode sleepArrayNode = sleepDataNode.path("sleep");
                if (sleepArrayNode.isArray() && sleepArrayNode.size() > 0) {

                    // Retrive Main Sleep record
                    JsonNode mainSleepNode = null;
                    for (JsonNode sleepNode : sleepArrayNode) {
                        if (sleepNode.path("isMainSleep").asBoolean()) {
                            mainSleepNode = sleepNode;
                            break;
                        }
                    }

                    if (mainSleepNode != null) {
                        int durationMillis = mainSleepNode.path("duration").asInt();
                        int efficiency = mainSleepNode.path("efficiency").asInt();

                        String startTimeString = mainSleepNode.path("startTime").asText();
                        String endTimeString = mainSleepNode.path("endTime").asText();

                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");
                        LocalDateTime startTime = LocalDateTime.parse(startTimeString, formatter);
                        LocalDateTime endTime = LocalDateTime.parse(endTimeString, formatter);

                        // Convert duration from milliseconds to hours and minutes
                        int durationMinutes = durationMillis / (1000 * 60);
                        int hours = durationMinutes / 60;
                        int minutes = durationMinutes % 60;
                        String durationString = hours + " hours " + minutes + " minutes";

                        sleepDTO.setSleepEfficiency(efficiency);
                        sleepDTO.setSleepDuration(durationString);
                        sleepDTO.setSleepEfficiency(efficiency);
                        sleepDTO.setStartTime(startTime);
                        sleepDTO.setEndTime(endTime);

                        return sleepDTO;
                    } else {
                        throw new SleepDataAbsentException();
                    }
                } else {
                    throw new SleepDataAbsentException();
                }

            } else {
                throw new SleepDataAbsentException();
            }
        } catch (JsonProcessingException e) {
            throw new FitbitFailException();
        } catch (SleepDataAbsentException e) {
            throw new SleepDataAbsentException();
        }
    }
}
