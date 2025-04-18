package com.project.WebTapGym.controllers;

import com.project.WebTapGym.dtos.ExerciseDTO;
import com.project.WebTapGym.dtos.ExerciseVideoDTO;
import com.project.WebTapGym.models.Exercise;
import com.project.WebTapGym.models.ExerciseVideo;
import com.project.WebTapGym.responses.ExerciseListResponse;
import com.project.WebTapGym.responses.ExerciseResponse;
import com.project.WebTapGym.services.ExerciseService;
import com.project.WebTapGym.services.IExerciseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;


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

    @PostMapping(value = "upload-video/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadVideo(
            @PathVariable("id") Long exerciseId,
            @RequestParam("file") MultipartFile file)
    {
        try{
            if (file.isEmpty()){
                return ResponseEntity.badRequest().body("File is empty");
            }

            if(exerciseId == null){
                return ResponseEntity.badRequest().body("Exercise id is null");
            }

            if (!file.getContentType().startsWith("video/")) {
                return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body("File must be a video");
            }

            String fileName = UUID.randomUUID() + "_" + StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
            java.nio.file.Path uploadDir = (java.nio.file.Path) Paths.get("exercise_videos");
            if (!Files.exists((java.nio.file.Path) uploadDir)) {
                Files.createDirectories((java.nio.file.Path) uploadDir);
            }

            java.nio.file.Path filePath = ((java.nio.file.Path) uploadDir).resolve(fileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            ExerciseVideoDTO exerciseVideoDTO = ExerciseVideoDTO
                    .builder()
                    .exerciseId(exerciseId)
                    .videoUrl(fileName)
                    .build();

            ExerciseVideo saved = exerciseService.createExerciseVideo(exerciseId, exerciseVideoDTO);
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
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
