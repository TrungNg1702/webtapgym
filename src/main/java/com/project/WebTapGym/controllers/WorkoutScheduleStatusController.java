package com.project.WebTapGym.controllers;

import com.project.WebTapGym.dtos.WorkoutStatisticsResponseDTO;
import com.project.WebTapGym.dtos.WorkoutStatusDTO;
import com.project.WebTapGym.services.IWorkoutScheduleStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${api.prefix}/workout-status")
@RequiredArgsConstructor
public class WorkoutScheduleStatusController {

    private final IWorkoutScheduleStatusService workoutStatusService;

    @PostMapping("/complete/{userId}")
    public ResponseEntity<?> markComplete(
            @PathVariable Long userId,
            @RequestBody WorkoutStatusDTO dto
    ) {
        try {
            workoutStatusService.markWorkoutCompleted(userId, dto);
            return ResponseEntity.ok("Workout marked as completed.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/user/{userId}/statistics")
    public ResponseEntity<WorkoutStatisticsResponseDTO> getWorkoutStatistics(@PathVariable Long userId) {
        WorkoutStatisticsResponseDTO response = workoutStatusService.getWorkoutStatistics(userId);
        return ResponseEntity.ok(response);
    }

}