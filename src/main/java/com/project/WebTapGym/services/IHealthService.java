package com.project.WebTapGym.services;

import com.project.WebTapGym.dtos.HealthRecordDTO;
import com.project.WebTapGym.responses.HealthChatResponse;
import com.project.WebTapGym.responses.HealthRecordResponse;

import java.util.List;
import java.util.Map;

public interface IHealthService {
    Map<String, Object> healthChat(Long userId,String bodyIndex, String goalInput);
    void saveRecord(Long userId, HealthRecordDTO request);
    List<HealthRecordResponse> getRecordHistory(Long userId);
    Map<String, Object> generateHealthAdvice(Long userId);

}
