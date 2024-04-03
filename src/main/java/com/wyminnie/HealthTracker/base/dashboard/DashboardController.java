package com.wyminnie.healthtracker.base.dashboard;

import java.io.IOException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import com.wyminnie.healthtracker.base.user.User;
import com.wyminnie.healthtracker.base.user.UserService;
import com.wyminnie.healthtracker.common.UserIDDTO;

import static com.wyminnie.healthtracker.common.ControllerUtils.notFound;
import static com.wyminnie.healthtracker.common.ControllerUtils.ok;
import static com.wyminnie.healthtracker.common.ControllerUtils.fail;

@RequestMapping("/dashboard")
@RestController
public class DashboardController {
    @Autowired
    DashboardService dashboardService;

    @Autowired
    UserService userService;

    @PostMapping("/")
    public Object getDashboardData(@RequestBody UserIDDTO dashboardFetchDTO) {
        try {
            User user = userService.findByUserId(Long.valueOf(dashboardFetchDTO.getUserId()));
            DashboardDTO dashboardDTO = new DashboardDTO();

            Map<String, Integer> hrv = dashboardService.getPreviousWeekHeartRateVariability(user);
            Map<String, Integer> steps = dashboardService.getPreviousWeekStepsCount(user);

            dashboardDTO.setHrv(hrv);
            dashboardDTO.setSteps(steps);

            return ok(dashboardDTO);

        } catch (Exception e) {
            return fail("SERVER_ERROR");
        }
    }

    @PostMapping("/BMI")
    public Object getBMI(@RequestBody UserIDDTO userIDDTO) {
        User user = userService.findByUserId(Long.valueOf(userIDDTO.getUserId()));
        if (user == null) {
            return notFound();
        }
        try {
            return ok(dashboardService.getBMI(user));
        } catch (ProfileNotCompleteException e) {
            return fail("PROFILE_NOT_COMPLETE");
        } catch (BMIInvalidException e) {
            return fail("BMI_INVALID");
        }
    }

    @PostMapping("/sharing")
    public Object sharing(@RequestBody CompleteRecordDTO completeRecordDTO) {
        User user = userService.findByUserId(Long.valueOf(completeRecordDTO.getUserId()));
        if (user == null) {
            return notFound();
        }
        try {

            return ok(dashboardService.getSharing(user.getUsername(), completeRecordDTO.getSteps(),
                    completeRecordDTO.getDays(), completeRecordDTO.getStartDate()));
        } catch (Error | IOException e) {
            return fail("SERVER_ERROR");
        }
    }
}
