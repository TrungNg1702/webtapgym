package com.project.WebTapGym.services;

import com.project.WebTapGym.responses.HealthChatResponse;

import java.util.Map;

public interface IHealthService {
    Map<String, Object> healthChat(Long userId,String bodyIndex, String goalInput);
}
