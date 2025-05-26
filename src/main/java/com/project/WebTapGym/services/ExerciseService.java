package com.project.WebTapGym.services;

import com.project.WebTapGym.dtos.ExerciseDTO;
import com.project.WebTapGym.dtos.ExerciseVideoDTO;
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

    // Định nghĩa thư mục gốc cho các uploads
    @Value("${upload.dir:uploads}") // Đọc từ application.properties, mặc định là "uploads"
    private String uploadDir;

    // Định nghĩa thư mục con cụ thể cho video bài tập
    private static final String EXERCISE_VIDEO_SUBDIRECTORY = "exercise_videos";


    @Override
    @Transactional
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
                .videoUrl(exerciseDTO.getVideoUrl()) // Nếu videoUrl được gửi từ DTO
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
                    .orElseThrow(() -> new RuntimeException("Muscle Group not found")));
            existingExercise.setMuscleSection(exerciseDTO.getMuscleSection());
            existingExercise.setTechniqueDescription(exerciseDTO.getTechniqueDescription());
            existingExercise.setEquipmentRequired(exerciseDTO.getEquipmentRequired());
            existingExercise.setTargetMusclePercentage(exerciseDTO.getTargetMusclePercentage());
            existingExercise.setRecommendedSets(exerciseDTO.getRecommendedSets());
            existingExercise.setRecommendedReps(exerciseDTO.getRecommendedReps());
            existingExercise.setVideoUrl(exerciseDTO.getVideoUrl()); // Cập nhật URL video từ DTO

            return exerciseRepository.save(existingExercise);
        }
        return null;
    }

    @Override
    @Transactional
    public void deleteExercise(long id) {
        Optional<Exercise> optionalExercise = exerciseRepository.findById(id);
        optionalExercise.ifPresent(exercise -> {
            // Xóa video và các ExerciseVideo liên quan nếu có
            if (exercise.getVideoUrl() != null && !exercise.getVideoUrl().isEmpty()) {
                try {
                    deleteFile(exercise.getVideoUrl(), EXERCISE_VIDEO_SUBDIRECTORY); // Sử dụng phương thức xóa file nội bộ
                } catch (Exception e) {
                    System.err.println("Failed to delete video file for exercise " + id + ": " + e.getMessage());
                }
            }
            List<ExerciseVideo> relatedVideos = exerciseVideoRepository.findByExerciseId(id);
            for (ExerciseVideo ev : relatedVideos) {
                try {
                    deleteFile(ev.getVideoUrl(), EXERCISE_VIDEO_SUBDIRECTORY); // Sử dụng phương thức xóa file nội bộ
                } catch (Exception e) {
                    System.err.println("Failed to delete exercise video file " + ev.getVideoUrl() + ": " + e.getMessage());
                }
                exerciseVideoRepository.delete(ev);
            }
            exerciseRepository.delete(exercise);
        });
    }

    @Override
    public boolean existsByName(String exerciseName) {
        return exerciseRepository.existsByName(exerciseName);
    }

    @Override
    @Transactional
    public ExerciseVideo createExerciseVideo(
            Long exerciseId,
            MultipartFile file) throws Exception {

        Exercise existingExercise = exerciseRepository.findById(exerciseId)
                .orElseThrow(() -> new RuntimeException("Exercise not found"));

        // *** LOGIC LƯU FILE ĐƯỢC DI CHUYỂN TRỰC TIẾP VÀO ĐÂY ***
        String fileName = storeFile(file, EXERCISE_VIDEO_SUBDIRECTORY); // Gọi phương thức lưu file nội bộ

        // Lưu tên file video vào trường video_url của Exercise (nếu bạn chỉ có 1 video chính)
        existingExercise.setVideoUrl(fileName);
        exerciseRepository.save(existingExercise);

        // Hoặc nếu bạn muốn lưu vào bảng ExerciseVideo riêng (cho nhiều video hoặc video phụ)
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

    // THÊM PHƯƠNG THỨC NÀY ĐỂ TRUYỀN VIDEO RA
    @Override
    public Resource getVideoAsResource(String fileName) throws Exception {
        return loadFileAsResource(fileName, EXERCISE_VIDEO_SUBDIRECTORY); // Gọi phương thức tải file nội bộ
    }


    private String storeFile(MultipartFile file, String subDirectory) throws IOException {
        String originalFilename = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        String fileName = UUID.randomUUID() + "_" + originalFilename;

        Path uploadPath = Paths.get(uploadDir, subDirectory).toAbsolutePath().normalize();
        Files.createDirectories(uploadPath); // Tạo thư mục nếu chưa tồn tại

        Path targetLocation = uploadPath.resolve(fileName);
        Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

        return fileName;
    }

    private Path getFilePath(String fileName, String subDirectory) {
        Path filePath = Paths.get(uploadDir, subDirectory, fileName).toAbsolutePath().normalize();
        return filePath;
    }


    private Resource loadFileAsResource(String fileName, String subDirectory) throws Exception {
        try {
            Path filePath = getFilePath(fileName, subDirectory);
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new Exception("File not found or not readable: " + fileName);
            }
        } catch (MalformedURLException ex) {
            throw new Exception("File not found or not readable: " + fileName, ex);
        }
    }


    private void deleteFile(String fileName, String subDirectory) throws Exception {
        Path filePath = getFilePath(fileName, subDirectory);
        if (Files.exists(filePath)) {
            Files.delete(filePath);
        } else {
            throw new Exception("File not found: " + fileName);
        }
    }
}