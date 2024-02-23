package com.wyminnie.healthtracker.base.recommendation;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wyminnie.healthtracker.base.user.User;
import com.wyminnie.healthtracker.base.user.UserService;
import com.wyminnie.healthtracker.common.UserIDDTO;

@RequestMapping("/recommendation")
@RestController
public class RecommendationController {
    @Autowired
    RecommendationService recommendationService;
    @Autowired
    UserService userService;

    @PostMapping("/update-preference")
    public ResponseEntity<String> saveUserPreference(@RequestBody UpdatePreferenceDTO updatePreferenceDTO) {

        User user = userService.findByUserId(Long.valueOf(updatePreferenceDTO.getUserId()));
        if (user == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User not found");
        }
        if (userService.updatePreference(user, updatePreferenceDTO.getPreference())) {
            return ResponseEntity.ok("User preference updated successfully");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("User preference update failed");
        }
    }

    @PostMapping("/recommneded-materials")
    public ResponseEntity<List<MaterialListItemDTO>> getRecommendedMaterials(
            @RequestBody UserIDDTO recommendMaterialDTO) {
        User user = userService.findByUserId(Long.valueOf(recommendMaterialDTO.getUserId()));
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

    @GetMapping(value = "/{materialId}")
    public MaterialDTO getMaterial(@PathVariable("materialId") String materialId) {
        return recommendationService.getMaterialById(Long.valueOf(materialId));
    }

}
