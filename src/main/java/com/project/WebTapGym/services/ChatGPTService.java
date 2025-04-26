package com.project.WebTapGym.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ChatGPTService {
    private final String geminiApiUrl;
    private final String apiKey;
    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    public ChatGPTService(
            @Value("${gemini.api.url}") String geminiApiUrl,
            @Value("${gemini.api.key}") String apiKey,
            WebClient.Builder webClientBuilder
    ) {
        this.geminiApiUrl = geminiApiUrl;
        this.apiKey = apiKey;
        this.webClient = webClientBuilder.baseUrl(geminiApiUrl).build();
        this.objectMapper = new ObjectMapper();
    }

    public String sendMessageToChatbot(String message) {
        // Tạo body theo định dạng Gemini API yêu cầu
        Map<String, Object> content = new HashMap<>();
        content.put("text", "Bạn là một huấn luyện viên thể hình chuyên nghiệp. Hãy trả lời câu hỏi sau liên quan đến tập gym, dinh dưỡng, hoặc lịch tập một cách chi tiết và dễ hiểu. Nếu câu hỏi không liên quan, hãy trả lời một cách chính xác và tự nhiên: " + message);

        Map<String, Object> part = new HashMap<>();
        part.put("parts", List.of(content));

        Map<String, Object> body = new HashMap<>();
        body.put("contents", List.of(part));

        // Gọi Gemini API
        Mono<String> responseMono = webClient.post()
                .uri(uriBuilder -> uriBuilder.queryParam("key", apiKey).build())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(String.class);

        // Blocking để lấy kết quả
        try {
            String response = responseMono.block();
            return parseResponse(response);
        } catch (Exception e) {
            return "Lỗi khi gọi API: " + e.getMessage();
        }
    }

    private String parseResponse(String jsonResponse) {
        try {
            JsonNode rootNode = objectMapper.readTree(jsonResponse);
            // Kiểm tra nếu có lỗi từ Gemini API
            if (rootNode.has("error")) {
                JsonNode errorNode = rootNode.get("error");
                String errorMessage = errorNode.has("message") ? errorNode.get("message").asText() : "Lỗi không xác định từ Gemini API";
                return "Lỗi từ Gemini API: " + errorMessage;
            }

            // Lấy text từ response
            JsonNode candidates = rootNode.get("candidates");
            if (candidates != null && candidates.isArray() && candidates.size() > 0) {
                JsonNode contentNode = candidates.get(0).get("content");
                if (contentNode != null) {
                    JsonNode partsNode = contentNode.get("parts");
                    if (partsNode != null && partsNode.isArray() && partsNode.size() > 0) {
                        JsonNode textNode = partsNode.get(0).get("text");
                        if (textNode != null) {
                            return textNode.asText();
                        }
                    }
                }
            }
            return "Không tìm thấy nội dung phản hồi từ Gemini API.";
        } catch (Exception e) {
            return "Lỗi khi parse phản hồi: " + e.getMessage();
        }
    }
}