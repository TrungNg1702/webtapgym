package com.project.WebTapGym.services;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class FoodAnalysisService implements IFoodAnalysisService {
    @Value("${gemini.api.url}")
    private String geminiApiUrl;

    @Value("${gemini.api.key}")
    private String apiKey;

    private final WebClient.Builder webClientBuilder;
    private final ObjectMapper objectMapper = new ObjectMapper();


    @Override
    public Map<String, Object> analyzeFood(String foodDescription) {
        WebClient webClient = webClientBuilder.baseUrl(geminiApiUrl).build();

        // Tao body theo yeu cau
        Map<String, Object> content = new HashMap<>();
        content.put("text", String.format(
                "Hãy phân tích món ăn sau và cung cấp các thông tin dinh dưỡng: %s",
                foodDescription
        ));

        Map<String, Object> part = new HashMap<>();
        part.put("parts", List.of(content));
        Map<String, Object> body = new HashMap<>();
        body.put("contents", List.of(part));

        // gui yeu cau cho gemini
        Mono<String> responseMno = webClient.post()
                .uri(uriBuilder -> uriBuilder.queryParam("key", apiKey).build())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(String.class);

        try{
            String response = responseMno.block();
            return parseResponse(response);
        } catch (Exception e){
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Loi khi goi Gemini API: " + e.getMessage());
            return errorResponse;
        }
    }
    private Map<String, Object> parseResponse(String jsonResponse) {
        try {
            JsonNode rootNode = objectMapper.readTree(jsonResponse);

            // Kết quả phân tích
            Map<String, Object> result = new HashMap<>();

            // Lấy text từ "candidates"
            JsonNode candidates = rootNode.get("candidates");
            if (candidates != null && candidates.isArray() && candidates.size() > 0) {
                JsonNode contentNode = candidates.get(0).get("content");
                if (contentNode != null) {
                    JsonNode partsNode = contentNode.get("parts");
                    if (partsNode != null && partsNode.isArray() && partsNode.size() > 0) {
                        JsonNode textNode = partsNode.get(0).get("text");
                        if (textNode != null) {
                            // Xử lý phản hồi ban đầu
                            String responseText = textNode.asText();

                            // Lọc thông tin ngắn gọn: lượng calo và gợi ý
                            String calories = extractCalories(responseText);
                            String suggestion = extractSuggestions(responseText);

                            // Tạo phản hồi với định dạng ngắn gọn
                            result.put("calories", calories);
                            result.put("suggestion", suggestion);
                            return result;
                        }
                    }
                }
            }

            result.put("error", "Không tìm thấy nội dung phản hồi từ Gemini API.");
            return result;
        } catch (Exception e) {
            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("error", "Lỗi khi phân tích phản hồi: " + e.getMessage());
            return errorResult;
        }
    }

    /**
     * Method để trích xuất lượng calo từ chuỗi phản hồi.
     */
    private String extractCalories(String responseText) {
        try {
            // Mở rộng Regex để tìm cụm từ liên quan đến lượng calo
            Pattern pattern = Pattern.compile("(?i)(\\d+)\\s*(c(al|alo)|kcal|kilo)");

            Matcher matcher = pattern.matcher(responseText);

            // Tìm và trích xuất lượng calo nếu match thành công
            if (matcher.find()) {
                return matcher.group(1) + " calo"; // Lấy số lượng calo, thêm từ "calo" vào
            }

            // Kiểm tra fallback nếu Regex không tìm thấy
            if (responseText.toLowerCase().contains("calorie") || responseText.toLowerCase().contains("calo")) {
                return "Chi tiết calo không rõ ràng trong mô tả.";
            }

            return "Không rõ"; // Nếu không tìm được thông tin calo
        } catch (Exception e) {
            return "Không rõ"; // Xử lý lỗi chung
        }
    }

    /**
     * Method để tạo gợi ý bổ sung dinh dưỡng ngắn gọn.
     */
    private String extractSuggestions(String responseText) {
        // Giả định phản hồi chứa cụm từ về gợi ý như "bổ sung protein" hoặc "chất béo"
        if (responseText.contains("protein")) {
            return "Bổ sung thêm thực phẩm giàu protein (như thịt, cá, trứng).";
        }
        if (responseText.contains("chất béo")) {
            return "Bổ sung chất béo lành mạnh (như dầu oliu, cá hồi, hạt).";
        }
        if (responseText.contains("carb")) {
            return "Thêm carbohydrate (như gạo, khoai, bánh mì).";
        }
        return "Không tìm thấy gợi ý cụ thể.";
    }


}