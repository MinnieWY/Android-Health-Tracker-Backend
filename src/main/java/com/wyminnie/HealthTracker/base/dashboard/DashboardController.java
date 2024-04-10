package com.wyminnie.healthtracker.base.dashboard;

import java.io.IOException;
import java.util.Map;

import org.hibernate.jdbc.Expectations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wyminnie.healthtracker.base.user.User;
import com.wyminnie.healthtracker.base.user.UserService;
import com.wyminnie.healthtracker.common.UserIDDTO;

import static com.wyminnie.healthtracker.common.ControllerUtils.notFound;
import static com.wyminnie.healthtracker.common.ControllerUtils.ok;
import static com.wyminnie.healthtracker.common.ControllerUtils.serverError;
import static com.wyminnie.healthtracker.common.ControllerUtils.fail;
import static com.wyminnie.healthtracker.common.ControllerUtils.ERR_SERVER_ERROR;

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
            return fail(ERR_SERVER_ERROR);
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
                    completeRecordDTO.getStartDate()));
        } catch (Exception e) {
            return serverError();
        }
    }

    @PostMapping("/sleep")
    public Object getSleepData(@RequestBody UserIDDTO userIDDTO) {
        User user = userService.findByUserId(Long.valueOf(userIDDTO.getUserId()));
        if (user == null) {
            return notFound();
        }

        try {
            return ok(dashboardService.getSleepData(user));
        } catch (SleepDataAbsentException e) {
            return fail("SLEEP_DATA_ABSENT");
        } catch (FitbitFailException e) {
            return serverError();
        }
    }

}
