package com.project.WebTapGym.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.WebTapGym.dtos.HealthRecordDTO;
import com.project.WebTapGym.models.HealthRecord;
import com.project.WebTapGym.models.User;
import com.project.WebTapGym.repositories.HealthRecordRepository;
import com.project.WebTapGym.repositories.UserRepository;
import com.project.WebTapGym.responses.HealthChatResponse;
import com.project.WebTapGym.responses.HealthRecordResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HealthService implements IHealthService{
    @Value("${gemini.api.url}")
    private String geminiApiUrl;

    @Value("${gemini.api.key}")
    private String apiKey;

    private final WebClient.Builder webClientBuilder;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final UserRepository userRepository;
    private final HealthRecordRepository healthRecordRepository;

    @Override
    public Map<String, Object> healthChat(Long userId, String bodyIndex, String goalInput) {
        WebClient webClient = webClientBuilder.baseUrl(geminiApiUrl).build();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String goal = (goalInput != null && !goalInput.isBlank()) ? goalInput : user.getGoal();

        if (goalInput != null && !goalInput.isBlank()) {
            user.setGoal(goalInput);
            userRepository.save(user);
        }

        Map<String, Object> content = new HashMap<>();
        content.put("text", String.format(
                "Bạn hãy đóng vai huấn luyện viên cá nhân và gợi ý dành cho người dùng (ví dụ: chế độ ăn, thời gian tập luyện) " +
                        "dựa trên Cân nặng, chiều cao, Chỉ số mỡ cơ thể, Lượng calo tiêu thụ và calo nạp vào mà người dùng mới nhập: %s, " +
                        "và người dùng có mục tiêu: %s. " +
                        "Hãy trình bày gợi ý một cách dễ hiểu như đang trò chuyện với người tập, không cần JSON, không cần đánh dấu markdown.",
                bodyIndex, goal));

        Map<String, Object> part = new HashMap<>();
        part.put("parts", List.of(content));

        Map<String, Object> body = new HashMap<>();
        body.put("contents", List.of(part));

        try {
            String rawResponse = webClient.post()
                    .uri(uriBuilder -> uriBuilder.queryParam("key", apiKey).build())
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block(); // gọi đồng bộ

            JsonNode jsonNode = objectMapper.readTree(rawResponse);
            String message = jsonNode
                    .path("candidates")
                    .get(0)
                    .path("content")
                    .path("parts")
                    .get(0)
                    .path("text")
                    .asText();

            return Map.of("message", message);

        } catch (Exception e) {
            return Map.of("error", "Đã xảy ra lỗi khi gọi Gemini API: " + e.getMessage());
        }
    }

    @Override
    public void saveRecord(Long userId, HealthRecordDTO request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        LocalDate today = LocalDate.now();

        if (healthRecordRepository.findByUserIdAndDate(userId, today).isPresent()) {
            throw new RuntimeException("Bạn đã nhập dữ liệu hôm nay rồi.");
        }

        HealthRecord record = HealthRecord.builder()
                .user(user)
                .date(today)
                .weight(request.getWeight())
                .height(request.getHeight())
                .bodyFat(request.getBodyFat())
                .caloriesIn(request.getCaloriesIn())
                .caloriesOut(request.getCaloriesOut())
                .note(request.getNote())
                .build();

        healthRecordRepository.save(record);
    }

    @Override
    public List<HealthRecordResponse> getRecordHistory(Long userId) {
        return healthRecordRepository.findAllByUserIdOrderByDateAsc(userId)
                .stream()
                .map(record -> {
                    HealthRecordResponse response = new HealthRecordResponse();
                    response.setDate(record.getDate());
                    response.setWeight(record.getWeight());
                    response.setBodyFat(record.getBodyFat());
                    response.setCaloriesIn(record.getCaloriesIn());
                    response.setCaloriesOut(record.getCaloriesOut());
                    response.setNote(record.getNote());
                    return response;
                }).collect(Collectors.toList());
    }

    @Override
    public Map<String, Object> generateHealthAdvice(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        HealthRecord record = healthRecordRepository.findTopByUserIdOrderByDateDesc(userId)
                .orElseThrow(() -> new RuntimeException("Chưa có dữ liệu sức khỏe để phân tích."));

        String goal = user.getGoal();
        if (goal == null || goal.isBlank()) {
            return Map.of("error", "Bạn chưa thiết lập mục tiêu cá nhân.");
        }

        String bodyIndex = String.format(
                "Chiều cao: %.1f cm, Cân nặng: %.1f kg, Chỉ số mỡ cơ thể: %.1f %%, " +
                        "Calo nạp vào: %d kcal, Calo tiêu thụ: %d kcal.",
                record.getHeight(), record.getWeight(), record.getBodyFat(),
                record.getCaloriesIn(), record.getCaloriesOut()
        );

        // Gửi sang Gemini AI
        WebClient webClient = webClientBuilder.baseUrl(geminiApiUrl).build();
        Map<String, Object> content = Map.of("text", String.format(
                "Bạn hãy đóng vai huấn luyện viên cá nhân và gợi ý cho người dùng dựa trên thông tin sau: %s " +
                        "và mục tiêu là: %s. Hãy trả lời như đang nói chuyện với người tập.",
                bodyIndex, goal));

        Map<String, Object> part = Map.of("parts", List.of(content));
        Map<String, Object> body = Map.of("contents", List.of(part));

        try {
            String raw = webClient.post()
                    .uri(uriBuilder -> uriBuilder.queryParam("key", apiKey).build())
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            JsonNode jsonNode = objectMapper.readTree(raw);
            String message = jsonNode.path("candidates").get(0)
                    .path("content").path("parts").get(0).path("text").asText();

            return Map.of("message", message);

        } catch (Exception e) {
            return Map.of("error", "Lỗi khi gọi AI: " + e.getMessage());
        }
    }

}
