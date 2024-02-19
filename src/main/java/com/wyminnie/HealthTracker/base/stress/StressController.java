package com.wyminnie.healthtracker.base.stress;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.wyminnie.healthtracker.base.user.User;
import com.wyminnie.healthtracker.base.user.UserService;

public class StressController {
    @Autowired
    private StressService stressService;
    @Autowired
    UserService userService;

    @PostMapping("/stress/input")
    public ResponseEntity<StressDTO> inputStressData(@RequestParam("userId") String userId,
            @RequestParam("date") String date,
            @RequestParam("stressLevel") int stressLevel) {

        User user = userService.findByUsername(userId);

        StressDTO savedRecord = stressService.createStressRecord(user.getId(), date, stressLevel);
        return ResponseEntity.ok(savedRecord);
    }
}
