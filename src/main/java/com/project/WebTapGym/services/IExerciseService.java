package com.project.WebTapGym.services;

import com.project.WebTapGym.dtos.ExerciseDTO;
import com.project.WebTapGym.dtos.ExerciseVideoDTO;
import com.project.WebTapGym.models.Exercise;
import com.project.WebTapGym.models.ExerciseVideo;
import com.project.WebTapGym.responses.ExerciseResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.*;

import java.util.List;


public interface IExerciseService {
    public Exercise createExercise(ExerciseDTO exerciseDTO);

    Exercise getExerciseById(long id);

    Page<ExerciseResponse> getAllExercises(PageRequest pageRequest);

    Exercise updateExercise(long exerciseId, ExerciseDTO exerciseDTO);

    void deleteExercise(long id);

    boolean existsByName(String exerciseName);

    ExerciseVideo createExerciseVideo(
            Long exerciseId,
            ExerciseVideoDTO dto) throws Exception;

    List<ExerciseResponse> getExercisesByMainMuscleGroup(Long muscleMainGroupId);

}
