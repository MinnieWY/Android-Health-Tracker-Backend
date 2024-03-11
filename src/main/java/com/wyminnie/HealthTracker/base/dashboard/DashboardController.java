package com.wyminnie.healthtracker.base.dashboard;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.wyminnie.healthtracker.base.user.User;
import com.wyminnie.healthtracker.base.user.UserService;
import com.wyminnie.healthtracker.common.UserIDDTO;

@RestController
public class DashboardController {
    @Autowired
    DashboardService dashboardService;

    @Autowired
    UserService userService;

    @PostMapping("/dashboard")
    public ResponseEntity<DashboardDTO> getDashboardData(@RequestBody UserIDDTO dashboardFetchDTO)
            throws JsonMappingException, JsonProcessingException {
        User user = userService.findByUserId(Long.valueOf(dashboardFetchDTO.getUserId()));
        DashboardDTO dashboardDTO = new DashboardDTO();

        Map<String, Integer> hrv = dashboardService.getPreviousWeekHeartRateVariability(user);
        Map<String, Integer> steps = dashboardService.getPreviousWeekStepsCount(user);

        dashboardDTO.setHrv(hrv);
        dashboardDTO.setSteps(steps);

        return ResponseEntity.ok(dashboardDTO);
    }
}
