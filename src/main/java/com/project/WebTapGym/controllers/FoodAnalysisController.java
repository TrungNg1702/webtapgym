package com.project.WebTapGym.controllers;

import com.project.WebTapGym.services.ChatGPTService;
import com.project.WebTapGym.services.IFoodAnalysisService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("${api.prefix}/food-analysis")
@RequiredArgsConstructor
public class FoodAnalysisController {
    private final IFoodAnalysisService foodAnalysisService;

    @PostMapping("/analyze")
    public ResponseEntity<?> analyzeFood(@RequestBody Map<String, String> request) {
        String foodDescription = request.get("foodDescription");

        if (foodDescription == null || foodDescription.isEmpty()) {
            return ResponseEntity.badRequest().body("Mô tả món ăn không được để trống.");
        }

        // Gọi service để phân tích món ăn
        Map<String, Object> analysisResult = foodAnalysisService.analyzeFood(foodDescription);

        if (analysisResult.containsKey("error")) {
            return ResponseEntity.status(500).body(analysisResult);
        }

        return ResponseEntity.ok(analysisResult);
    }

}
