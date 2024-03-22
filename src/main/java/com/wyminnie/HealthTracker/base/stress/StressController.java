package com.wyminnie.healthtracker.base.stress;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.wyminnie.healthtracker.base.user.User;
import com.wyminnie.healthtracker.base.user.UserService;
import com.wyminnie.healthtracker.common.UserIDDTO;

import org.springframework.web.bind.annotation.GetMapping;

import static com.wyminnie.healthtracker.common.ControllerUtils.fail;
import static com.wyminnie.healthtracker.common.ControllerUtils.ok;

@RestController
@RequestMapping("/stress")
public class StressController {
    @Autowired
    private StressService stressService;
    @Autowired
    UserService userService;

    @PostMapping("/input")
    public ResponseEntity<StressDTO> inputStressData(@RequestBody StressInputDTO stressInputDTO) {
        User user = userService.findByUserId(Long.valueOf(stressInputDTO.getUserId()));
        if (user == null) {
            return ResponseEntity.badRequest().build();
        }

        StressDTO savedRecord = stressService.createStressRecord(user.getId(), stressInputDTO.getDate(),
                stressInputDTO.getStressLevel());
        return ResponseEntity.ok(savedRecord);
    }

    @PostMapping("/today")
    public ResponseEntity<Integer> getTodayStress(@RequestBody UserIDDTO userIDDTO) {
        User user = userService.findByUserId(Long.valueOf(userIDDTO.getUserId()));
        if (user == null) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(stressService.getTodayStress(user.getId()));
    }

    @PostMapping("/weekly")
    public ResponseEntity<Map<String, Integer>> getPreviousWeekStress(
            @RequestBody UserIDDTO userIDDTO) {
        User user = userService.findByUserId(Long.valueOf(userIDDTO.getUserId()));
        if (user == null) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(stressService.getPreviousWeekStress(user.getId()));
    }

    @PostMapping("/prediction")
    public Object getMethodName(@RequestBody UserIDDTO userIddto) {
        User user = userService.findByUserId(Long.valueOf(userIddto.getUserId()));

        try {
            return ok(stressService.predictStressLevel(user.getAccessToken()));
        } catch (MLFailedException e) {
            return fail("ERROR_ML_FAILED");
        }
    }

}
