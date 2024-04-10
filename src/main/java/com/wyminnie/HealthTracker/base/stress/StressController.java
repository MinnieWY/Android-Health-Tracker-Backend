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
        } catch (StressRecordAlreadyExist e) {
            return fail("ERROR_STRESS_RECORD_EXIST");
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

    @PostMapping("/date")
    public Object getStressByDate(@RequestBody StressDateDTO stressDateDTO) {
        User user = userService.findByUserId(Long.valueOf(stressDateDTO.getUserId()));
        if (user == null) {
            return notFound();
        }
        try {
            return ok(stressService.getTodayStress(user.getId()));
        } catch (Exception e) {
            return fail("ERROR_GET_DATE_STRESS_FAILED");
        }
    }

    @PostMapping("/trend")
    public Object getStressTrend(@RequestBody UserIDDTO userIDDTO) {
        User user = userService.findByUserId(Long.valueOf(userIDDTO.getUserId()));
        if (user == null) {
            return notFound();
        }
        try {
            return ok(stressService.getStresTrend(user.getId()));
        } catch (NoStressRecordException e) {
            return fail("ERROR_NO_STRESS_RECORD");
        } catch (Exception e) {
            return fail("ERROR_GET_WEEKLY_STRESS_FAILED");
        }
    }

    @PostMapping("/monthly")
    public Object getMonthStress(@RequestBody StressMonthDTO stressMonthDTO) {
        User user = userService.findByUserId(Long.valueOf(stressMonthDTO.getUserId()));
        if (user == null) {
            return notFound();
        }
        try {
            return ok(stressService.getMonthStress(user.getId(), stressMonthDTO.getMonth(),
                    stressMonthDTO.getYear()));
        } catch (Exception e) {
            return fail("ERROR_GET_WEEKLY_STRESS_FAILED");
        }
    }

    @PostMapping("/prediction")
    public Object predictStressLevel(@RequestBody UserIDDTO userIddto) {
        User user = userService.findByUserId(Long.valueOf(userIddto.getUserId()));

        try {
            return ok(stressService.predictStressLevel(user.getId(), user.getAccessToken()));
        } catch (MLFailedException e) {
            return fail(e.getMessage());
        }
    }

}
