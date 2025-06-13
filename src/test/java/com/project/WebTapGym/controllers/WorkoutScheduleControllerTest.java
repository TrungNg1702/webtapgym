package com.project.WebTapGym.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.WebTapGym.TestSecurityConfig;
import com.project.WebTapGym.dtos.WorkoutScheduleDTO;
import com.project.WebTapGym.models.WorkoutSchedule;
import com.project.WebTapGym.responses.WorkoutScheduleChatResponse;
import com.project.WebTapGym.responses.WorkoutScheduleResponse;
import com.project.WebTapGym.services.IWorkoutScheduleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.reset;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Import({TestSecurityConfig.class, WorkoutScheduleControllerTest.MockConfig.class})
@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(controllers = WorkoutScheduleController.class)
class WorkoutScheduleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private IWorkoutScheduleService workoutScheduleService;

    @Autowired
    private ObjectMapper objectMapper;

    @TestConfiguration
    static class MockConfig {
        @Bean
        public IWorkoutScheduleService workoutScheduleService() {
            return Mockito.mock(IWorkoutScheduleService.class);
        }
    }

    @BeforeEach
    void setup() {
        reset(workoutScheduleService);
    }

    @Test
    void testCreateWorkoutSchedule() throws Exception {
        WorkoutScheduleDTO dto = new WorkoutScheduleDTO();
        // Bạn có thể set giá trị mẫu cho DTO nếu muốn

        WorkoutSchedule mockSchedule = new WorkoutSchedule();
        Mockito.when(workoutScheduleService.createWorkoutSchedule(any()))
                .thenReturn(mockSchedule);

        mockMvc.perform(post("/api/v1/workout-schedules")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    void testGetWorkoutSchedulesByUserId() throws Exception {
        WorkoutScheduleResponse res1 = new WorkoutScheduleResponse();
        WorkoutScheduleResponse res2 = new WorkoutScheduleResponse();
        List<WorkoutScheduleResponse> responses = Arrays.asList(res1, res2);
        Page<WorkoutScheduleResponse> page = new PageImpl<>(responses, PageRequest.of(0, 2), 2);

        Mockito.when(workoutScheduleService.getWorkoutSchedulesByUserId(eq(1L), any(PageRequest.class)))
                .thenReturn(page);

        mockMvc.perform(get("/api/v1/workout-schedules/user/1")
                        .param("page", "0")
                        .param("limit", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.workoutSchedules.length()").value(2));
    }

    @Test
    void testDeleteWorkoutSchedule() throws Exception {
        Mockito.doNothing().when(workoutScheduleService).deleteWorkoutSchedule(1L);

        mockMvc.perform(delete("/api/v1/workout-schedules/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Workout schedule deleted successfully"));
    }

    @Test
    void testSuggestWorkoutSchedule() throws Exception {
        WorkoutScheduleChatResponse response = new WorkoutScheduleChatResponse();
        response.setMessage("Test suggestion");

        Mockito.when(workoutScheduleService.suggestWorkoutSchedule(eq(1L), any()))
                .thenReturn(response);

        mockMvc.perform(get("/api/v1/workout-schedules/suggest")
                        .param("userId", "1")
                        .param("goal", "gain muscle"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Test suggestion"));
    }
}
