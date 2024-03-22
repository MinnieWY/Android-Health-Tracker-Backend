package com.wyminnie.healthtracker.base.stress;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wyminnie.healthtracker.base.user.User;
import com.wyminnie.healthtracker.base.user.UserService;
import com.wyminnie.healthtracker.common.UserIDDTO;

import static com.wyminnie.healthtracker.common.ControllerUtils.fail;
import static com.wyminnie.healthtracker.common.ControllerUtils.notFound;
import static com.wyminnie.healthtracker.common.ControllerUtils.ok;

@RestController
@RequestMapping("/stress")
public class StressController {
    @Autowired
    private StressService stressService;
    @Autowired
    UserService userService;

    @PostMapping("/input")
    public Object inputStressData(@RequestBody StressInputDTO stressInputDTO) {
        User user = userService.findByUserId(Long.valueOf(stressInputDTO.getUserId()));
        if (user == null) {
            return notFound();
        }
        try {
            return ok(stressService.createStressRecord(user.getId(), stressInputDTO.getDate(),
                    stressInputDTO.getStressLevel()));
        } catch (Exception e) {
            return fail("ERROR_STRESS_RECORD_FAILED");
        }
    }

    @PostMapping("/today")
    public Object getTodayStress(@RequestBody UserIDDTO userIDDTO) {
        User user = userService.findByUserId(Long.valueOf(userIDDTO.getUserId()));
        if (user == null) {
            return notFound();
        }
        try {
            return ok(stressService.getTodayStress(user.getId()));
        } catch (Exception e) {
            return fail("ERROR_GET_TODAY_STRESS_FAILED");
        }
    }

    @PostMapping("/weekly")
    public Object getPreviousWeekStress(@RequestBody UserIDDTO userIDDTO) {
        User user = userService.findByUserId(Long.valueOf(userIDDTO.getUserId()));
        if (user == null) {
            return notFound();
        }
        try {
            return ok(stressService.getPreviousWeekStress(user.getId()));
        } catch (Exception e) {
            return fail("ERROR_GET_WEEKLY_STRESS_FAILED");
        }
    }

    @PostMapping("/prediction")
    public Object predictStressLevel(@RequestBody UserIDDTO userIddto) {
        User user = userService.findByUserId(Long.valueOf(userIddto.getUserId()));

        try {
            return ok(stressService.predictStressLevel(user.getAccessToken()));
        } catch (MLFailedException e) {
            return fail("ERROR_ML_FAILED");
        }
    }

}
