package com.wyminnie.healthtracker.base.stress;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.wyminnie.healthtracker.base.user.User;
import com.wyminnie.healthtracker.base.user.UserService;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/stress")
public class StressController {
    @Autowired
    private StressService stressService;
    @Autowired
    UserService userService;

    @PostMapping("/input")
    public ResponseEntity<StressDTO> inputStressData(@RequestParam("userId") String userId,
            @RequestParam("date") String date,
            @RequestParam("stressLevel") int stressLevel) {

        User user = userService.findByUsername(userId);

        StressDTO savedRecord = stressService.createStressRecord(user.getId(), date, stressLevel);
        return ResponseEntity.ok(savedRecord);
    }

    @GetMapping("/today")
    public ResponseEntity<Integer> getTodayStress(@RequestParam("userId") String userId) {
        User user = userService.findByUsername(userId);

        return ResponseEntity.ok(stressService.getTodayStress(user.getId()));
    }

    @GetMapping("/weekly")
    public ResponseEntity<Map<String, Integer>> getWeeklyStress(@RequestParam("userId") String userId,
            @RequestParam("date") String date) {

        User user = userService.findByUsername(userId);

        Map<String, Integer> stress_record = stressService.getWeeklyStress(user.getId(), date);

        return ResponseEntity.ok(stress_record);
    }

    @GetMapping("/prediction")
    public ResponseEntity<Integer> getMethodName(@RequestParam("userId") String userId) {
        User user = userService.findByUsername(userId);

        return ResponseEntity.ok(stressService.predictStressLevel(user.getAccessToken()));
    }

}
