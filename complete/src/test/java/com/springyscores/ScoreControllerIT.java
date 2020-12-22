package com.springyscores;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.springyscores.models.ScoreModel;
import com.springyscores.services.ScoreService;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class ScoreControllerIT {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ScoreService scoreService;

    @MockBean
    ScoreRepository scoreRepository;

    @Test
    public void whenCreateScore_ScoreIsCreatedAndReturned() throws Exception {
        ScoreModel scoreModel = new ScoreModel("khurl", 1000, new Date());
        when(scoreRepository.save(any(ScoreModel.class))).thenReturn(scoreModel);

        mockMvc.perform(MockMvcRequestBuilders.post("/createscore")
                .content("{\"player\":\"khurl\",\"score\":6000,\"time\":\"2020-12-24\"}")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.player").value("khurl"));
    }

    @Test
    public void whenCreateScoreWithNegativeScore_thenScoreNotCreated() throws Exception {
        ScoreModel scoreModel = new ScoreModel("khurl", -100, new Date());

        mockMvc.perform(MockMvcRequestBuilders.post("/createscore")
                .content("User score must be positive")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void whenGetScoreById_thenGetReturnsScoreModel() throws Exception {
        ScoreModel scoreModel = new ScoreModel("khurl", 2000, new Date());
        when(scoreRepository.findById(3L)).thenReturn(Optional.of(scoreModel));

        mockMvc.perform(MockMvcRequestBuilders.get("/3")
                .content("{\"player\":\"khurl\",\"score\":2000,\"time\":\"2020-12-24\"}")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.score").value("2000"));
    }

    @Test
    public void whenGetScoreByIdWithNoRecord_thenGetReturnsNoScoreModel() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/3")
                .content("User score doesn't exist")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void whenDeleteScoreByIdWithRecord_thenDeleteReturnsOk() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/1")
                .content("1")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void whenGetPlayerHistoryWithPlayer_thenGetReturnsOk() throws Exception {
        ScoreModel scoreModel = new ScoreModel("khurl", 10000, new Date());
        ScoreModel scoreModel1 = new ScoreModel("kHUrl", 12000, new Date());
        List<ScoreModel> scores = new ArrayList<>();
        scores.add(scoreModel);
        scores.add(scoreModel1);
        when(scoreRepository.findByPlayerOrderByScore("KHURL")).thenReturn(Optional.of(scores));

        mockMvc.perform(MockMvcRequestBuilders.get("/history/KHURL")
                .content("[{\"id\":1,\"player\":\"khurl\",\"score\":10000,\"time\":\"2020-12-24T00:00:00.000+00:00\"},{\"id\":2,\"player\":\"kHUrl\",\"score\":12000,\"time\":\"2020-12-24T00:00:00.000+00:00\"}]")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void whenGetPlayerHistoryWithInvalidPlayer_thenGetReturnsNoModels() throws Exception {
        ScoreModel scoreModel = new ScoreModel("khurl", 10000, new Date());
        ScoreModel scoreModel1 = new ScoreModel("kHUrl", 12000, new Date());
        List<ScoreModel> scores = new ArrayList<>();
        scores.add(scoreModel);
        scores.add(scoreModel1);
        when(scoreRepository.findByPlayerOrderByScore("khurl")).thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders.get("/history/khurl")
                .content("User has no scores")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
