package com.project.WebTapGym.controllers;

import com.project.WebTapGym.dtos.WorkoutScheduleDTO;
import com.project.WebTapGym.models.WorkoutSchedule;
import com.project.WebTapGym.services.IWorkoutScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/workout-schedules")
@RequiredArgsConstructor
public class WorkoutScheduleController {

    private final IWorkoutScheduleService workoutScheduleService;

    // API Tạo lịch tập
    @PostMapping("")
    public ResponseEntity<?> createWorkoutSchedule(@RequestBody WorkoutScheduleDTO workoutScheduleDTO) {
        try {
            WorkoutSchedule createdSchedule = workoutScheduleService.createWorkoutSchedule(workoutScheduleDTO);
            return ResponseEntity.ok(createdSchedule);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    // API Lấy lịch tập của người dùng
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getWorkoutSchedulesByUserId(@PathVariable Long userId) {
        try {
            List<WorkoutSchedule> schedules = workoutScheduleService.getWorkoutSchedulesByUserId(userId);
            return ResponseEntity.ok(schedules);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    // API Xóa lịch tập theo ID
    @DeleteMapping("/{scheduleId}")
    public ResponseEntity<?> deleteWorkoutSchedule(@PathVariable Long scheduleId) {
        try {
            workoutScheduleService.deleteWorkoutSchedule(scheduleId);
            return ResponseEntity.ok("Workout schedule deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    // API Gợi ý lịch tập
    @GetMapping("/suggest")
    public ResponseEntity<?> suggestWorkoutSchedule(
            @RequestParam Long userId,
            @RequestParam String goal
    ) {
        try {
            List<WorkoutSchedule> suggestedSchedules = workoutScheduleService.suggestWorkoutSchedule(userId, goal);
            return ResponseEntity.ok(suggestedSchedules);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
}