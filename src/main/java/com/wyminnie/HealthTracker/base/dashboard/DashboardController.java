package com.wyminnie.healthtracker.base.dashboard;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.wyminnie.healthtracker.base.user.User;
import com.wyminnie.healthtracker.base.user.UserService;

@RestController
public class DashboardController {
    @Autowired
    DashboardService dashboardService;

    @Autowired
    UserService userService;

    @GetMapping("/dashboard")
    public ResponseEntity<DashboardDTO> getDashboardData() throws JsonMappingException, JsonProcessingException {
        DashboardDTO dashboardDTO = new DashboardDTO();

        User user = userService.findByUsername("admin");

        Map<String, Integer> hrv = dashboardService.getPreviousWeekHeartRateVariability(user);
        Map<String, Integer> steps = dashboardService.getPreviousWeekStepsCount(user);

        dashboardDTO.setHrv(hrv);
        dashboardDTO.setSteps(steps);

        return ResponseEntity.ok(dashboardDTO);
    }
}
