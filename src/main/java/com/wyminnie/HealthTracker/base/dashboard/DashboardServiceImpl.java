package com.wyminnie.healthtracker.base.dashboard;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.NumberFormat;
import java.io.IOException;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
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
        double denominator = (user.getHeight() / 100) * (user.getHeight() / 100);

        double bmi = Math.round((user.getWeight() / denominator) * 10.0) / 10.0;
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

    @Override
    public byte[] getSharing(String username, Integer steps, Integer days, String date) throws IOException {

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

        if (days > 1) {
            String text_text_days = "for " + days + " days";
            int x_text_days = 340;
            int y_text_days = 830;
            g2d.drawString(text_text_days, x_text_days, y_text_days);

            String text_text_date = "since " + date;
            int x_text_date = 250;
            int y_text_date = 950;
            g2d.drawString(text_text_date, x_text_date, y_text_date);
        } else {

            String text_text = "on " + date;
            int x_text = 300;
            int y_text = 950;
            g2d.drawString(text_text, x_text, y_text);
        }

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
}
