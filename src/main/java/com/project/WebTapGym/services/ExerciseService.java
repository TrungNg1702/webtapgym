package com.project.WebTapGym.services;

import com.project.WebTapGym.dtos.ExerciseDTO;
import com.project.WebTapGym.models.Exercise;
import com.project.WebTapGym.models.ExerciseVideo;
import com.project.WebTapGym.models.MuscleGroup;
import com.project.WebTapGym.repositories.ExerciseRepository;
import com.project.WebTapGym.repositories.ExerciseVideoRepository;
import com.project.WebTapGym.repositories.MuscleGroupsRepository;
import com.project.WebTapGym.responses.ExerciseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExerciseService implements IExerciseService {

    private final ExerciseRepository exerciseRepository;
    private final MuscleGroupsRepository muscleGroupsRepository;
    private final ExerciseVideoRepository exerciseVideoRepository;

    @Value("${upload.dir:uploads}")
    private String uploadDir;

    private static final String EXERCISE_VIDEO_SUBDIRECTORY = "exercise_videos";

    @Override
    @Transactional
    public Exercise createExercise(ExerciseDTO exerciseDTO) {
        MuscleGroup existingMuscleGroup = muscleGroupsRepository
                .findById(exerciseDTO.getMuscleGroupId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nhóm cơ."));

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
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bài tập."));
    }

    @Override
    public Page<ExerciseResponse> getAllExercises(PageRequest pageRequest) {
        return exerciseRepository
                .findAll(pageRequest)
                .map(ExerciseResponse::fromExercise);
    }

    @Override
    @Transactional
    public Exercise updateExercise(long exerciseId, ExerciseDTO exerciseDTO) {
        Exercise existingExercise = getExerciseById(exerciseId);
        if (existingExercise != null) {
            existingExercise.setName(exerciseDTO.getExerciseName());
            existingExercise.setMuscleGroup(muscleGroupsRepository.findById(exerciseDTO.getMuscleGroupId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy nhóm cơ.")));
            existingExercise.setMuscleSection(exerciseDTO.getMuscleSection());
            existingExercise.setTechniqueDescription(exerciseDTO.getTechniqueDescription());
            existingExercise.setEquipmentRequired(exerciseDTO.getEquipmentRequired());
            existingExercise.setTargetMusclePercentage(exerciseDTO.getTargetMusclePercentage());
            existingExercise.setRecommendedSets(exerciseDTO.getRecommendedSets());
            existingExercise.setRecommendedReps(exerciseDTO.getRecommendedReps());
            existingExercise.setRestBetweenSets(exerciseDTO.getRestBetweenSets());

            return exerciseRepository.save(existingExercise);
        }
        return null;
    }

    @Override
    @Transactional
    public void deleteExercise(long id) {
        Optional<Exercise> optionalExercise = exerciseRepository.findById(id);
        optionalExercise.ifPresent(exercise -> {
            // Xóa tất cả các ExerciseVideo liên quan đến Exercise
            List<ExerciseVideo> relatedVideos = exerciseVideoRepository.findByExerciseId(id);
            for (ExerciseVideo ev : relatedVideos) {
                try {
                    deleteFile(ev.getVideoUrl(), EXERCISE_VIDEO_SUBDIRECTORY);
                } catch (IOException e) {
                    System.err.println("Không thể xóa tệp video bài tập " + ev.getVideoUrl() + ": " + e.getMessage());
                    throw new RuntimeException("Lỗi khi xóa tệp video bài tập liên quan " + ev.getVideoUrl(), e);
                }
                exerciseVideoRepository.delete(ev);
            }
            exerciseRepository.delete(exercise); // Xóa bản ghi Exercise
        });
    }

    @Override
    @Transactional
    public void deleteExerciseVideo(long exerciseVideoId) {
        Optional<ExerciseVideo> optionalExerciseVideo = exerciseVideoRepository.findById(exerciseVideoId);
        if (optionalExerciseVideo.isPresent()) {
            ExerciseVideo exerciseVideo = optionalExerciseVideo.get();
            try {
                deleteFile(exerciseVideo.getVideoUrl(), EXERCISE_VIDEO_SUBDIRECTORY);
                exerciseVideoRepository.delete(exerciseVideo);
            } catch (IOException e) {
                System.err.println("Không thể xóa video bài tập với ID " + exerciseVideoId + ": " + e.getMessage());
                throw new RuntimeException("Lỗi khi xóa tệp video bài tập hoặc bản ghi.", e);
            }
        } else {
            throw new RuntimeException("Không tìm thấy video bài tập với ID " + exerciseVideoId + ".");
        }
    }

    @Override
    public boolean existsByName(String exerciseName) {
        return exerciseRepository.existsByName(exerciseName);
    }

    @Override
    @Transactional
    public ExerciseVideo createExerciseVideo(
            Long exerciseId,
            MultipartFile file) throws IOException {

        Exercise existingExercise = exerciseRepository.findById(exerciseId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bài tập."));

        String fileName = storeFile(file, EXERCISE_VIDEO_SUBDIRECTORY);

        // Chỉ lưu vào bảng ExerciseVideo
        ExerciseVideo exerciseVideo = ExerciseVideo.builder()
                .exercise(existingExercise)
                .videoUrl(fileName)
                .build();

        return exerciseVideoRepository.save(exerciseVideo);
    }

    @Override
    public List<ExerciseResponse> getExercisesByMainMuscleGroup(Long muscleMainGroupId) {
        return exerciseRepository
                .findByMuscleMainGroupId(muscleMainGroupId)
                .stream()
                .map(ExerciseResponse::fromExercise)
                .collect(Collectors.toList());
    }

    @Override
    public Resource getVideoAsResource(String fileName) throws IOException {
        return loadFileAsResource(fileName, EXERCISE_VIDEO_SUBDIRECTORY);
    }

    private String storeFile(MultipartFile file, String subDirectory) throws IOException {
        String originalFilename = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        String fileName = UUID.randomUUID() + "_" + originalFilename;

        Path uploadPath = Paths.get(uploadDir, subDirectory).toAbsolutePath().normalize();
        Files.createDirectories(uploadPath);

        Path targetLocation = uploadPath.resolve(fileName);
        Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

        return fileName;
    }

    private Path getFilePath(String fileName, String subDirectory) {
        return Paths.get(uploadDir, subDirectory, fileName).toAbsolutePath().normalize();
    }

    private Resource loadFileAsResource(String fileName, String subDirectory) throws IOException {
        try {
            Path filePath = getFilePath(fileName, subDirectory);
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                return resource;
            } else {
                throw new IOException("Không tìm thấy tệp hoặc không thể đọc: " + fileName);
            }
        } catch (MalformedURLException ex) {
            throw new IOException("Lỗi khi tạo URL cho tệp: " + fileName, ex);
        }
    }

    private void deleteFile(String fileName, String subDirectory) throws IOException {
        if (fileName == null || fileName.isEmpty()) {
            return;
        }
        Path filePath = getFilePath(fileName, subDirectory);
        if (Files.exists(filePath)) {
            try {
                Files.delete(filePath);
            } catch (IOException e) {
                System.err.println("Không thể xóa tệp " + fileName + ". Lý do: " + e.getMessage());
                throw e;
            }
        } else {
            System.out.println("Không tìm thấy tệp để xóa: " + fileName + ". Có thể đã bị xóa trước đó.");
        }
    }
}