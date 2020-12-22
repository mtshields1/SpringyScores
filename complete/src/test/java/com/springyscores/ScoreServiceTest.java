package com.springyscores;

import com.springyscores.models.HistoryModel;
import com.springyscores.models.ScoreHistory;
import com.springyscores.models.ScoreModel;
import com.springyscores.services.ScoreService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
public class ScoreServiceTest {

    @Autowired
    ScoreService scoreService;

    @MockBean
    ScoreRepository scoreRepository;

    @Test
    public void whenSaveScore_thenScoreSavedSuccessfully() throws Exception {
        ScoreModel scoreModel = new ScoreModel("khurl", 1000, new Date());
        when(scoreRepository.save(any(ScoreModel.class))).thenReturn(scoreModel);

        ScoreModel madeModel = scoreService.saveScore(scoreModel);

        assertThat(madeModel.getScore()).isEqualTo(scoreModel.getScore());
        assertThat(madeModel.getPlayer()).isEqualTo(scoreModel.getPlayer());
    }

    @Test
    public void whenGetScoreById_thenReturnCorrectScore() throws Exception {
        ScoreModel scoreModel = new ScoreModel("khurl", 2000, new Date());
        when(scoreRepository.findById(any(Long.class))).thenReturn(Optional.of(scoreModel));

        Optional<ScoreModel> foundModel = scoreService.getScoreById(1L);

        assertThat(foundModel.get().getScore()).isEqualTo(scoreModel.getScore());
        assertThat(foundModel.get().getPlayer()).isEqualTo(scoreModel.getPlayer());
    }

    @Test
    public void whenGetScoreByIdWithBadId_thenReturnEmptyScore() throws Exception {
        when(scoreRepository.findById(any(Long.class))).thenReturn(Optional.empty());

        Optional<ScoreModel> foundModel = scoreService.getScoreById(1L);

        assertThat(foundModel).isEqualTo(Optional.empty());
    }

    @Test
    public void whenGetPlayerHistoryWithNoHistory_thenReturnEmptyScore() throws Exception {
        when(scoreRepository.findByPlayerOrderByScore(any(String.class))).thenReturn(Optional.empty());

        ResponseEntity responseEntity = scoreService.getPlayerHistory("khurl");

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void whenGetPlayerHistory_thenReturnsProperData() throws Exception {
        ScoreModel scoreModel1 = new ScoreModel("khurl", 1000, new Date());
        ScoreModel scoreModel2 = new ScoreModel("khurl", 3000, new Date());
        ScoreModel scoreModel3 = new ScoreModel("khurl", 4500, new Date());
        List<ScoreModel> scores = new ArrayList<>();
        scores.add(scoreModel1);
        scores.add(scoreModel2);
        scores.add(scoreModel3);
        HistoryModel historyModel = new HistoryModel();
        historyModel.setLowScore(scoreModel1.getScore(), scoreModel1.getTime());
        historyModel.setTopScore(scoreModel3.getScore(), scoreModel3.getTime());
        historyModel.setAverageScore((double) 8500/3);
        historyModel.addScoreToHistoryList(scoreModel1.getScore(), scoreModel1.getTime());
        historyModel.addScoreToHistoryList(scoreModel2.getScore(), scoreModel2.getTime());
        historyModel.addScoreToHistoryList(scoreModel3.getScore(), scoreModel3.getTime());
        when(scoreRepository.findByPlayerOrderByScore(any(String.class))).thenReturn(Optional.of(scores));

        ResponseEntity responseEntity = scoreService.getPlayerHistory("khurl");

        HistoryModel returnedModel = (HistoryModel) responseEntity.getBody();
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(returnedModel.getLowScore().getScore()).isEqualTo(historyModel.getLowScore().getScore());
        assertThat(returnedModel.getTopScore().getScore()).isEqualTo(historyModel.getTopScore().getScore());
        assertThat(returnedModel.getAverageScore()).isEqualTo(historyModel.getAverageScore());
        List<ScoreHistory> returnedScores = returnedModel.getPlayerScores();
        List<ScoreHistory> sentScores = historyModel.getPlayerScores();
        assertTrue(Objects.equals(returnedScores.get(0).getScore(), sentScores.get(0).getScore()));
        assertTrue(Objects.equals(returnedScores.get(1).getScore(), sentScores.get(1).getScore()));
        assertTrue(Objects.equals(returnedScores.get(2).getScore(), sentScores.get(2).getScore()));
    }

    @Test
    public void whenGetScoreListWithJustPlayersAndBiggerPagination_thenReturnsProperData() throws Exception {
        ScoreModel scoreModel1 = new ScoreModel("khurl", 1000, new Date());
        ScoreModel scoreModel2 = new ScoreModel("dan", 3000, new Date());
        ScoreModel scoreModel3 = new ScoreModel("khurl", 4500, new Date());
        List<ScoreModel> scores = new ArrayList<>();
        scores.add(scoreModel1);
        scores.add(scoreModel2);
        scores.add(scoreModel3);
        Map<String, String> filters = new HashMap<>();
        filters.put("players", "khurl, dan");
        filters.put("pageno", "0");
        filters.put("pagesize", "4");
        Pageable paging = PageRequest.of(Integer.parseInt(filters.get("pageno")), Integer.parseInt(filters.get("pagesize")));
        List<String> namesRequested = Arrays.asList("khurl, dan".split("\\s*,\\s*"));
        Slice<ScoreModel> slice = new SliceImpl<ScoreModel>(scores);
        when(scoreRepository.findByPlayerIn(namesRequested, paging)).thenReturn(slice);
        List<ScoreModel> foundScores = scoreService.getListOfScores(filters);
        assertTrue(foundScores.size() == 3);
    }

    @Test
    public void whenGetScoreListWithJustPlayersAndSmallPageSize_thenReturnsProperData() throws Exception {
        ScoreModel scoreModel1 = new ScoreModel("khurl", 1000, new Date());
        ScoreModel scoreModel2 = new ScoreModel("dan", 3000, new Date());
        ScoreModel scoreModel3 = new ScoreModel("khurl", 4500, new Date());
        List<ScoreModel> scores = new ArrayList<>();
        scores.add(scoreModel1);
        scores.add(scoreModel2);
        scores.add(scoreModel3);
        Map<String, String> filters = new HashMap<>();
        filters.put("players", "khurl, dan");
        filters.put("pageno", "0");
        filters.put("pagesize", "3");
        Pageable paging = PageRequest.of(Integer.parseInt(filters.get("pageno")), Integer.parseInt(filters.get("pagesize")));
        List<String> namesRequested = Arrays.asList("khurl, dan".split("\\s*,\\s*"));
        Slice<ScoreModel> slice = new SliceImpl<>(scores, paging, true);
        when(scoreRepository.findByPlayerIn(namesRequested, paging)).thenReturn(slice);
        List<ScoreModel> foundScores = scoreService.getListOfScores(filters);
        assertTrue(foundScores.size() == 3);
    }

    @Test
    public void whenGetScoreListWithMultiplePages_thenReturnsProperData() throws Exception {
        ScoreModel scoreModel1 = new ScoreModel("khurl", 1000, new Date());
        ScoreModel scoreModel2 = new ScoreModel("dan", 3000, new Date());
        ScoreModel scoreModel3 = new ScoreModel("khurl", 4500, new Date());
        ScoreModel scoreModel4 = new ScoreModel("dan", 9000, new Date());
        List<ScoreModel> scores1 = new ArrayList<>();
        scores1.add(scoreModel1);
        scores1.add(scoreModel2);
        scores1.add(scoreModel3);
        List<ScoreModel> scores2 = new ArrayList<>();
        scores2.add(scoreModel4);
        Map<String, String> filters1 = new HashMap<>();
        filters1.put("players", "khurl, dan");
        filters1.put("pageno", "0");
        filters1.put("pagesize", "3");
        Pageable paging1 = PageRequest.of(Integer.parseInt(filters1.get("pageno")), Integer.parseInt(filters1.get("pagesize")));
        List<String> namesRequested1 = Arrays.asList("khurl, dan".split("\\s*,\\s*"));
        Slice<ScoreModel> slice = new SliceImpl<>(scores1, paging1, true);
        when(scoreRepository.findByPlayerIn(namesRequested1, paging1)).thenReturn(slice);
        List<ScoreModel> foundScores = scoreService.getListOfScores(filters1);
        Map<String, String> filters2 = new HashMap<>();
        filters2.put("players", "khurl, dan");
        filters2.put("pageno", "1");
        filters2.put("pagesize", "3");
        Pageable paging2 = PageRequest.of(Integer.parseInt(filters2.get("pageno")), Integer.parseInt(filters2.get("pagesize")));
        List<String> namesRequested2 = Arrays.asList("khurl, dan".split("\\s*,\\s*"));
        Slice<ScoreModel> slice2 = new SliceImpl<>(scores2, paging2, false);
        when(scoreRepository.findByPlayerIn(namesRequested2, paging2)).thenReturn(slice2);
        List<ScoreModel> foundScores2 = scoreService.getListOfScores(filters2);
        assertTrue(foundScores2.size() == 1); // Page 2 contains one record
    }

    @Test
    public void whenGetScoreListWithDateBetweenAndAfter_thenReturnsProperData() throws Exception {
        DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date dateAfter = inputFormat.parse("2020-12-20");
        Date dateBetween = inputFormat.parse("2020-12-23");
        Date dateBefore = inputFormat.parse("2020-12-25");
        ScoreModel scoreModelBetween = new ScoreModel("dan", 3000, dateBetween);
        List<ScoreModel> scores = new ArrayList<>();
        scores.add(scoreModelBetween);
        Map<String, String> filters = new HashMap<>();
        filters.put("players", "khurl, dan");
        filters.put("pageno", "0");
        filters.put("pagesize", "3");
        filters.put("datebefore", "2020-12-25");  // Get data between these dates
        filters.put("dateafter", "2020-12-20");
        Pageable paging = PageRequest.of(Integer.parseInt(filters.get("pageno")), Integer.parseInt(filters.get("pagesize")));
        List<String> namesRequested = Arrays.asList("khurl, dan".split("\\s*,\\s*"));
        Slice<ScoreModel> slice = new SliceImpl<ScoreModel>(scores);
        when(scoreRepository.findByPlayerInAndTimeAfterAndTimeBefore(namesRequested, dateAfter, dateBefore, paging)).thenReturn(slice);
        List<ScoreModel> foundScores = scoreService.getListOfScores(filters);
        assertTrue(foundScores.size() == 1);
        assertTrue(foundScores.get(0).getScore() == 3000);  // The score between the 2 requested dates
    }

    @Test
    public void whenGetScoreListWithRequestedDateAfter_thenReturnsProperData() throws Exception {
        DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date dateAfter = inputFormat.parse("2020-12-20");
        Date dateBetween = inputFormat.parse("2020-12-23");
        Date dateBefore = inputFormat.parse("2020-12-25");
        ScoreModel scoreModelBetween = new ScoreModel("dan", 3000, dateBetween);
        ScoreModel scoreModelAfter = new ScoreModel("khurl", 4500, dateBefore);
        List<ScoreModel> scores = new ArrayList<>();
        scores.add(scoreModelBetween);
        scores.add(scoreModelAfter);
        Map<String, String> filters = new HashMap<>();
        filters.put("players", "khurl, dan");
        filters.put("pageno", "0");
        filters.put("pagesize", "3");
        filters.put("dateafter", "2020-12-20");  // Get data after this date
        Pageable paging = PageRequest.of(Integer.parseInt(filters.get("pageno")), Integer.parseInt(filters.get("pagesize")));
        List<String> namesRequested = Arrays.asList("khurl, dan".split("\\s*,\\s*"));
        Slice<ScoreModel> slice = new SliceImpl<ScoreModel>(scores);
        when(scoreRepository.findByPlayerInAndTimeAfter(namesRequested, dateAfter, paging)).thenReturn(slice);
        List<ScoreModel> foundScores = scoreService.getListOfScores(filters);
        assertTrue(foundScores.size() == 2);
        assertTrue(foundScores.get(0).getScore() == 3000);  // The two scores after the requested date
        assertTrue(foundScores.get(1).getScore() == 4500);
    }

    @Test
    public void whenGetScoreListWithRequestedDateBefore_thenReturnsProperData() throws Exception {
        DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date dateAfter = inputFormat.parse("2020-12-20");
        Date dateBetween = inputFormat.parse("2020-12-23");
        Date dateBefore = inputFormat.parse("2020-12-25");
        ScoreModel scoreModelBefore = new ScoreModel("khurl", 1000, dateAfter);
        ScoreModel scoreModelBetween = new ScoreModel("dan", 4200, dateBetween);
        List<ScoreModel> scores = new ArrayList<>();
        scores.add(scoreModelBefore);
        scores.add(scoreModelBetween);
        Map<String, String> filters = new HashMap<>();
        filters.put("players", "khurl, dan");
        filters.put("pageno", "0");
        filters.put("pagesize", "3");
        filters.put("datebefore", "2020-12-25");  // Get data before this date
        Pageable paging = PageRequest.of(Integer.parseInt(filters.get("pageno")), Integer.parseInt(filters.get("pagesize")));
        List<String> namesRequested = Arrays.asList("khurl, dan".split("\\s*,\\s*"));
        Slice<ScoreModel> slice = new SliceImpl<ScoreModel>(scores);
        when(scoreRepository.findByPlayerInAndTimeBefore(namesRequested, dateBefore, paging)).thenReturn(slice);
        List<ScoreModel> foundScores = scoreService.getListOfScores(filters);
        assertTrue(foundScores.size() == 2);
        assertTrue(foundScores.get(0).getScore() == 1000);  // The two scores before the requested date
        assertTrue(foundScores.get(1).getScore() == 4200);
    }
}
