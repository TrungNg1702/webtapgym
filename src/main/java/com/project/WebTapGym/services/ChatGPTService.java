package com.project.WebTapGym.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ChatGPTService {
    @Value("${openai.api.key}")
    private String apiKey;

    @Value("${openai.api.url}")
    private String openaiUrl;

    @Value("${openai.api.referer}")
    private String openaiReferer;

    public String askChatGPT(String message) throws Exception {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", "Bearer " + apiKey);
        headers.add("HTTP-Referer", openaiReferer);

        Map<String, Object> body = new HashMap<>();
        body.put("model", "openai/gpt-3.5-turbo"); // hoặc bạn chọn model khác nếu muốn

        List<Map<String, String>> messages = List.of(
                Map.of("role", "system", "content", "Bạn là huấn luyện viên gym, trả lời ngắn gọn, dễ hiểu."),
                Map.of("role", "user", "content", message)
        );

        body.put("messages", messages);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(openaiUrl, entity, Map.class);
        List<Map<String, Object>> choices = (List<Map<String, Object>>) response.getBody().get("choices");
        Map<String, Object> messageData = (Map<String, Object>) choices.get(0).get("message");

        return messageData.get("content").toString();
     }
}
