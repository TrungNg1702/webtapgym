package com.project.WebTapGym.services;

import com.project.WebTapGym.dtos.WorkoutStatusDTO;
import com.project.WebTapGym.models.User;
import com.project.WebTapGym.models.WorkoutSchedule;
import com.project.WebTapGym.models.WorkoutScheduleStatus;
import com.project.WebTapGym.repositories.UserRepository;
import com.project.WebTapGym.repositories.WorkoutScheduleRepository;
import com.project.WebTapGym.repositories.WorkoutScheduleStatusRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class WorkoutScheduleStatusService implements IWorkoutScheduleStatusService{
    private final WorkoutScheduleStatusRepository statusRepo;
    private final WorkoutScheduleRepository scheduleRepo;
    private final UserRepository userRepo;

    @Override
    public void markWorkoutCompleted(Long userId, WorkoutStatusDTO dto) {
        WorkoutSchedule schedule = scheduleRepo.findById(dto.getScheduleId())
                .orElseThrow(() -> new RuntimeException("Schedule not found"));

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        WorkoutScheduleStatus status = statusRepo
                .findBySchedule_IdAndUser_Id(dto.getScheduleId(), userId)
                .orElse(WorkoutScheduleStatus.builder()
                        .schedule(schedule)
                        .user(user)
                        .build());

        status.setCompleted(dto.getCompleted());
        status.setCompletedAt(LocalDateTime.now());

        statusRepo.save(status);
    }
}
