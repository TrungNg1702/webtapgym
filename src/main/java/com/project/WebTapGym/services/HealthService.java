package com.project.WebTapGym.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.WebTapGym.models.User;
import com.project.WebTapGym.repositories.UserRepository;
import com.project.WebTapGym.responses.HealthChatResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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


}
