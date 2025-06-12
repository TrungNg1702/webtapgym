package com.project.WebTapGym.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.WebTapGym.dtos.WorkoutScheduleDTO;
import com.project.WebTapGym.models.Exercise;
import com.project.WebTapGym.models.User;
import com.project.WebTapGym.models.WorkoutSchedule;
import com.project.WebTapGym.repositories.ExerciseRepository;
import com.project.WebTapGym.repositories.UserRepository;
import com.project.WebTapGym.repositories.WorkoutScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
@Service
@RequiredArgsConstructor
public class WorkoutScheduleService implements IWorkoutScheduleService
{
    private final ChatGPTService chatGPTService;
    private final WorkoutScheduleRepository workoutScheduleRepository;
    private final UserRepository userRepository;
    private final ExerciseRepository  exerciseRepository;


    @Override
    @Transactional
    public WorkoutSchedule createWorkoutSchedule(WorkoutScheduleDTO workoutScheduleDTO) {
        String phone = SecurityContextHolder.getContext().getAuthentication().getName(); // Lấy phone từ SecurityContext

        User user = userRepository.findById(workoutScheduleDTO.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));


        List<Exercise> exercises = new ArrayList<>();
        if (workoutScheduleDTO.getExerciseIds() != null && !workoutScheduleDTO.getExerciseIds().isEmpty()) {
            for (Long exerciseId : workoutScheduleDTO.getExerciseIds()) {
                Exercise exercise = exerciseRepository.findById(exerciseId)
                        .orElseThrow(() -> new RuntimeException("Exercise not found with ID: " + exerciseId));
                exercises.add(exercise);
            }
        }


        WorkoutSchedule workoutSchedule = WorkoutSchedule.builder()
                .userId(user)
                .dayOfWeek(workoutScheduleDTO.getDayOfWeek())
                .timeSlot(workoutScheduleDTO.getTimeSlot())
                .workoutType(workoutScheduleDTO.getWorkoutType())
                .duration(workoutScheduleDTO.getDuration())
                .exercises(exercises)
                .build();
        return workoutScheduleRepository.save(workoutSchedule);
    }

    @Override
    @Transactional
    public List<WorkoutSchedule> getWorkoutSchedulesByUserId(Long userId) {
        return workoutScheduleRepository.findByUserId_Id(userId);
    }

    @Override
    public void deleteWorkoutSchedule(Long scheduleId) {
        WorkoutSchedule workoutSchedule = workoutScheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new RuntimeException("Workout schedule not found"));
        workoutScheduleRepository.delete(workoutSchedule);
    }

    @Override
    public List<WorkoutSchedule> suggestWorkoutSchedule(Long userId, String goal) {
        // gui yeu cau den gemini
        String message = "Hãy gợi ý lịch tập luyện trong 1 tuần cho người muốn " + goal +
                ". Mỗi ngày bao gồm: ngày trong tuần, buổi tập (sáng, chiều), loại hình tập luyện (ví dụ: cardio, tạ, nghỉ), và thời lượng (phút). Trả về dưới dạng JSON dạng danh sách.";
        String aiResponse = chatGPTService.sendMessageToChatbot(message);

        // Parse JSON từ response AI
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode root = objectMapper.readTree(aiResponse);

            if (root.isArray()) {
                User user = userRepository.findById(userId)
                        .orElseThrow(() -> new RuntimeException("User not found"));

                List<WorkoutSchedule> schedules = new java.util.ArrayList<>();
                for (JsonNode node : root) {
                    WorkoutSchedule schedule = WorkoutSchedule.builder()
                            .dayOfWeek(node.get("day").asText())
                            .timeSlot(node.get("time").asText())
                            .workoutType(node.get("type").asText())
                            .duration(node.get("duration").asInt())
                            .build();
                    schedules.add(schedule);
                }

                workoutScheduleRepository.saveAll(schedules);
                return schedules;
            } else {
                throw new RuntimeException("AI không trả về định dạng JSON đúng");
            }

        } catch (Exception e) {
            throw new RuntimeException("Không thể phân tích phản hồi AI: " + e.getMessage());
        }
    }
}
