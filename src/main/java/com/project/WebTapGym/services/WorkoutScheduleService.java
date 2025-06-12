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
import com.project.WebTapGym.responses.WorkoutScheduleChatResponse;
import com.project.WebTapGym.responses.WorkoutScheduleResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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

        User user = userRepository.findByPhone(phone)
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
    public Page<WorkoutScheduleResponse> getWorkoutSchedulesByUserId(Long userId, PageRequest pageRequest) {
        return workoutScheduleRepository
                .findByUserId_Id(userId, pageRequest)
                .map(WorkoutScheduleResponse::fromWorkoutSchedule);
    }

    @Override
    public void deleteWorkoutSchedule(Long scheduleId) {
        WorkoutSchedule workoutSchedule = workoutScheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new RuntimeException("Workout schedule not found"));
        workoutScheduleRepository.delete(workoutSchedule);
    }

    @Override
    public WorkoutScheduleChatResponse suggestWorkoutSchedule(Long userId, String goalInput) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String goal = (goalInput != null && !goalInput.isBlank()) ? goalInput : user.getGoal();

        if (goalInput != null && !goalInput.isBlank()) {
            user.setGoal(goalInput);
            userRepository.save(user);
        }

        String message = "Bạn hãy đóng vai huấn luyện viên cá nhân và gợi ý một lịch tập luyện kéo dài 1 tuần cho người có mục tiêu: " + goal +
                ". Mỗi ngày nên gồm: thứ trong tuần, thời gian tập, loại hình tập, thời lượng. Hãy trình bày gợi ý một cách dễ hiểu như đang trò chuyện với người tập, không cần JSON, không cần đánh dấu markdown.";

        String aiResponse = chatGPTService.sendMessageToChatbot(message);

        return new WorkoutScheduleChatResponse(aiResponse.trim());
    }


}
