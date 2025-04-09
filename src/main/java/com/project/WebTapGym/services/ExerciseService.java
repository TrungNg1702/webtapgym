package com.project.WebTapGym.services;

import com.project.WebTapGym.dtos.ExerciseDTO;
import com.project.WebTapGym.models.Exercise;
import com.project.WebTapGym.models.MuscleGroup;
import com.project.WebTapGym.repositories.ExerciseRepository;
import com.project.WebTapGym.repositories.MuscleGroupsRepository;
import com.project.WebTapGym.responses.ExerciseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ExerciseService implements IExerciseService {

    private final ExerciseRepository exerciseRepository;
    private final MuscleGroupsRepository muscleGroupsRepository;
    @Override
    public Exercise createExercise(ExerciseDTO exerciseDTO) {
        MuscleGroup existingMuscleGroup = muscleGroupsRepository
                .findById(exerciseDTO.getMuscleGroupId())
                .orElseThrow(() -> new RuntimeException("Muscle Group not found"));


        Exercise newExercise = Exercise.builder()
                .name(exerciseDTO.getExerciseName())
                .muscleGroup(existingMuscleGroup)
                .muscleSection(exerciseDTO.getMuscleSection())
                .techniqueDescription(exerciseDTO.getTechniqueDescription())
                .equipmentRequired(exerciseDTO.getEquipmentRequired())
                .targetMusclePercentage(exerciseDTO.getTargetMusclePercentage())
                .recommendedSets(exerciseDTO.getRecommendedSets())
                .recommendedReps(exerciseDTO.getRecommendedReps())
                .restBetweenSets(exerciseDTO.getRestBetweenSets())
                .build();
        return exerciseRepository.save(newExercise);
    }

    @Override
    public Exercise getExerciseById(long exerciseId) {
        return exerciseRepository.findById(exerciseId)
                .orElseThrow(() -> new RuntimeException("Exercise not found"));
    }

    @Override
    public Page<ExerciseResponse> getAllExercises(PageRequest pageRequest) {
        // Lấy danh sách bài tập theo trang và giới hạn
        return exerciseRepository
                .findAll(pageRequest)
                .map(ExerciseResponse::fromExercise);
    }

    @Override
    public Exercise updateExercise(long exerciseId, ExerciseDTO exerciseDTO) {
        Exercise existingExercise = getExerciseById(exerciseId);
        if (existingExercise != null) {
            existingExercise.setName(exerciseDTO.getExerciseName());
            existingExercise.setMuscleGroup(muscleGroupsRepository.findById(exerciseDTO.getMuscleGroupId())
                    .orElseThrow(() -> new RuntimeException("Muscle Group not found")));
            existingExercise.setMuscleSection(exerciseDTO.getMuscleSection());
            existingExercise.setTechniqueDescription(exerciseDTO.getTechniqueDescription());
            existingExercise.setEquipmentRequired(exerciseDTO.getEquipmentRequired());
            existingExercise.setTargetMusclePercentage(exerciseDTO.getTargetMusclePercentage());
            existingExercise.setRecommendedSets(exerciseDTO.getRecommendedSets());
            existingExercise.setRecommendedReps(exerciseDTO.getRecommendedReps());

            return exerciseRepository.save(existingExercise);
        }

        return null;
    }

    @Override
    public void deleteExercise(long id) {
        Optional<Exercise> optionalExercise = exerciseRepository.findById(id);
        optionalExercise.ifPresent(exerciseRepository::delete);

    }

    @Override
    public boolean existsByName(String exerciseName) {
        return exerciseRepository.existsByName(exerciseName);
    }
}
