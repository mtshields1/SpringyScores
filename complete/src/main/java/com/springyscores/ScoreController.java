package com.springyscores;

import com.springyscores.models.ScoreModel;
import com.springyscores.services.ScoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
public class ScoreController {

	@Autowired
	ScoreRepository scoreRepository;
	@Autowired
	ScoreService scoreService;

	@PostMapping(path="/createscore", consumes = "application/json", produces = "application/json")
	@ResponseBody
	public ResponseEntity createScore(@RequestBody ScoreModel scoreModel) {
		if (scoreModel.getScore() < 0) { return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User score must be positive"); } // Need a positive score
		scoreModel.caseInsensitive();
		scoreModel = scoreService.saveScore(scoreModel);
		return new ResponseEntity<ScoreModel>(scoreModel, HttpStatus.OK);
	}

	@GetMapping(path="/{id}")
	@ResponseBody
	public ResponseEntity getSingleScore(@PathVariable Long id) {
		Optional<ScoreModel> scoreModel = scoreService.getScoreById(id);
		if (scoreModel.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User score doesn't exist");
		}
		return new ResponseEntity<>(scoreModel.get(), HttpStatus.OK);
	}

	@GetMapping(path="/history/{player}")
	@ResponseBody
	public ResponseEntity getPlayerHistory(@PathVariable String player) {
		return scoreService.getPlayerHistory(player);
	}

	@DeleteMapping("/{id}")
	public void delete(@PathVariable Long id) {
		scoreService.deleteScore(id);
	}

	@GetMapping(path="/list")
	@ResponseBody
	public List<ScoreModel> getListOfScores(@RequestParam Map<String, String> filters) throws ParseException {
		return scoreService.getListOfScores(filters);
	}
}
