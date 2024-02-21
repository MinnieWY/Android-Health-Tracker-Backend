package com.wyminnie.healthtracker.base.recommendation;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.wyminnie.healthtracker.base.user.User;
import com.wyminnie.healthtracker.base.user.UserService;

@RequestMapping("/recommendation")
@RestController
public class RecommendationController {
    @Autowired
    RecommendationService recommendationService;
    @Autowired
    UserService userService;

    @PostMapping("/update-preference")
    public ResponseEntity<String> saveUserPreference(@RequestParam("userId") String userId,
            @RequestParam("preference") int preference) {

        User user = userService.findByUserId(Long.valueOf(userId));
        if (user == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User not found");
        }
        if (userService.updatePreference(user, preference)) {
            return ResponseEntity.ok("User preference updated successfully");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("User preference update failed");
        }
    }

    @GetMapping("/recommneded-materials")
    public ResponseEntity<List<MaterialListItemDTO>> getRecommendedMaterials(
            @RequestParam(name = "userId") String userId) {
        User user = userService.findByUserId(Long.valueOf(userId));
        if (user == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        String preference = user.getPreference();
        return ResponseEntity.ok(recommendationService.getRecommendedsMaterials(preference));
    }

    @GetMapping("/list")
    public ResponseEntity<List<MaterialListItemDTO>> getMaterialsList() {

        return ResponseEntity.ok(recommendationService.getMaterialsList());
    }

    @GetMapping("path")
    public MaterialDTO getMaterial(@RequestParam("id") String materialId) {
        return recommendationService.getMaterialById(Long.valueOf(materialId));
    }

}
