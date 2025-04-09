package com.project.WebTapGym.controllers;

import com.project.WebTapGym.dtos.ExerciseDTO;
import com.project.WebTapGym.models.Exercise;
import com.project.WebTapGym.responses.ExerciseListResponse;
import com.project.WebTapGym.responses.ExerciseResponse;
import com.project.WebTapGym.services.ExerciseService;
import com.project.WebTapGym.services.IExerciseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("${api.prefix}/exercises")
@RequiredArgsConstructor
public class ExerciseController {

    private final IExerciseService exerciseService;
    @PostMapping("")
    public ResponseEntity<?> createExercise(
            @Valid @RequestBody ExerciseDTO exerciseDTO,
            BindingResult result)
    {
        try{
            if (result.hasErrors()) {
                List<String> errorMessages= result.getFieldErrors()
                        .stream()
                        .map(FieldError::getDefaultMessage)
                        .toList();
                return ResponseEntity.badRequest().body(errorMessages);
            }

            exerciseService.createExercise(exerciseDTO);
            return ResponseEntity.ok("tao moi thanh cong");
        } catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @GetMapping("")
    public ResponseEntity<ExerciseListResponse> getExercises(
            @RequestParam("page") int page,
            @RequestParam("limit") int limit
    ) {
        PageRequest pageRequest = PageRequest.of(page, limit,
                Sort.by("createdAt").descending());
        Page<ExerciseResponse> exercisePage = exerciseService.getAllExercises(pageRequest);
        // Lay ra tong so trang
        int totalPages = exercisePage.getTotalPages();
        List<ExerciseResponse> exercises = exercisePage.getContent();

        return ResponseEntity.ok(ExerciseListResponse.builder()
                        .exercises(exercises)
                        .totalPages(totalPages)
                        .build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getExercisesById(
            @PathVariable("id") Long exerciseId)
    {
        try{
            Exercise existingExercise = exerciseService.getExerciseById(exerciseId);
            return ResponseEntity.ok(ExerciseResponse.fromExercise(existingExercise));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteExerciseById(
            @PathVariable("id") Long exerciseId)
    {
        try{
            exerciseService.deleteExercise(exerciseId);
            return ResponseEntity.ok(String.format("Deleted exercise with id: %d", exerciseId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }


    @PutMapping("/{id}")
    public ResponseEntity<?> updateExercise(
            @PathVariable("id") Long exerciseId,
            @Valid @RequestBody ExerciseDTO exerciseDTO,
            BindingResult result
    ) {
        try {
            if (result.hasErrors()) {
                List<String> errorMessages = result.getFieldErrors()
                        .stream()
                        .map(FieldError::getDefaultMessage)
                        .toList();
                return ResponseEntity.badRequest().body(errorMessages);
            }

            Exercise updatedExercise = exerciseService.updateExercise(exerciseId, exerciseDTO);
            return ResponseEntity.ok(ExerciseResponse.fromExercise(updatedExercise));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
