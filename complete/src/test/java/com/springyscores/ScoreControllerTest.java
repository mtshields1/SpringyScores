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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class ScoreControllerTest {

	@Autowired
	MockMvc mockMvc;

	@MockBean
	ScoreService scoreService;

	@Test
	public void whenCreateScore_thenPostReturnsOk() throws Exception {
		ScoreModel scoreModel = new ScoreModel("khurl", 1000, new Date());
		when(scoreService.saveScore(any(ScoreModel.class))).thenReturn(scoreModel);

		mockMvc.perform(MockMvcRequestBuilders.post("/createscore")
				.content("{\"player\":\"khurl\",\"score\":6000,\"time\":\"2020-12-24\"}")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
	}

	@Test
	public void whenCreateScoreWithNegativeScore_thenPostReturnsBadRequest() throws Exception {

		mockMvc.perform(MockMvcRequestBuilders.post("/createscore")
				.content("{\"player\":\"khurl\",\"score\":-1000,\"time\":\"2020-12-24\"}")
				.content("User score must be positive")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void whenGetScoreById_thenGetReturnsScoreModel() throws Exception {
		ScoreModel scoreModel = new ScoreModel("khurl", 2000, new Date());
		when(scoreService.getScoreById(3L)).thenReturn(Optional.of(scoreModel));

		mockMvc.perform(MockMvcRequestBuilders.get("/3")
				.content("{\"player\":\"khurl\",\"score\":2000,\"time\":\"2020-12-24\"}")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.score").value("2000"));
	}

	@Test
	public void whenGetScoreByIdWithNoRecord_thenGetReturnsNotFound() throws Exception {
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
	public void whenGetListScoreWithPlayers_thenGetReturnsOk() throws Exception {
		ScoreModel scoreModel = new ScoreModel("khurl", 3000, new Date());
		ScoreModel scoreModel2 = new ScoreModel("meke", 4000, new Date());
		Map<String, String> paramMap = new HashMap<>();
		paramMap.put("players", "khurl,dan");
		paramMap.put("pageno", "0");
		paramMap.put("pagesize", "3");
		List<ScoreModel> playersScores = new ArrayList<>();
		playersScores.add(scoreModel);
		playersScores.add(scoreModel2);
		when(scoreService.getListOfScores(paramMap)).thenReturn(playersScores);

		mockMvc.perform(MockMvcRequestBuilders.get("/list?players=khurl,meke")
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
		when(scoreService.getPlayerHistory("khurl")).thenReturn(new ResponseEntity(scores, HttpStatus.OK));

		mockMvc.perform(MockMvcRequestBuilders.get("/history/KHURL")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
	}

	@Test
	public void whenGetPlayerHistoryWithInvalidPlayer_thenGetReturnsNotFound() throws Exception {
		when(scoreService.getPlayerHistory("khurl")).thenReturn(new ResponseEntity(HttpStatus.NOT_FOUND));

		mockMvc.perform(MockMvcRequestBuilders.get("/history/khurl")
				.content("User has no scores")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound());
	}
}