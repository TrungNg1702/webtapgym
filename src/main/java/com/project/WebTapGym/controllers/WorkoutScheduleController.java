package com.project.WebTapGym.controllers;

import com.project.WebTapGym.dtos.WorkoutScheduleDTO;
import com.project.WebTapGym.models.WorkoutSchedule;
import com.project.WebTapGym.responses.WorkoutScheduleChatResponse;
import com.project.WebTapGym.responses.WorkoutScheduleListResponse;
import com.project.WebTapGym.responses.WorkoutScheduleResponse;
import com.project.WebTapGym.services.IWorkoutScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
    public ResponseEntity<WorkoutScheduleListResponse> getWorkoutSchedulesByUserId(
            @PathVariable Long userId,
            @RequestParam("page") int page,
            @RequestParam("limit") int limit) {

            PageRequest pageRequest = PageRequest.of(page, limit);
            Page<WorkoutScheduleResponse> workoutSchedule  = workoutScheduleService.getWorkoutSchedulesByUserId( userId,pageRequest);

            int totalPages = workoutSchedule.getTotalPages();
            List<WorkoutScheduleResponse> workoutSchedules = workoutSchedule.getContent();

            return ResponseEntity.ok(WorkoutScheduleListResponse
                    .builder()
                            .workoutSchedules(workoutSchedules)
                            .totalPages(totalPages)
                    .build());
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
            @RequestParam(required = false) String goal // optional
    ) {
        try {
            WorkoutScheduleChatResponse response = workoutScheduleService.suggestWorkoutSchedule(userId, goal);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }


}