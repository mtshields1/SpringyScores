package com.springyscores;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@RestController
public class ScoreController {

	@Autowired
	ScoreRepository scoreRepository;

	@PostMapping(path="/createscore", consumes = "application/json", produces = "application/json")
	@ResponseBody
	public ResponseEntity createScore(@RequestBody ScoreModel scoreModel) {
		if (scoreModel.getScore() < 0) { return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User score must be positive"); } // Need a positive score
		scoreModel.caseInsensitive();
		scoreRepository.save(scoreModel);
		return new ResponseEntity<ScoreModel>(scoreModel, HttpStatus.OK);
	}

	@GetMapping(path="/{id}")
	@ResponseBody
	public ResponseEntity getSingleScore(@PathVariable Long id) {
		Optional<ScoreModel> scoreModel = scoreRepository.findById(id);
		if (scoreModel.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User score doesn't exist");
		}
		return new ResponseEntity<>(scoreModel.get(), HttpStatus.OK);
	}

	@GetMapping(path="/history/{player}")
	@ResponseBody
	public ResponseEntity getPlayerHistory(@PathVariable String player) {
		Optional<List<ScoreModel>> history = scoreRepository.findByPlayerOrderByScore(player);
		// TODO: create model to return. need to filter scores, highest, lowest, etc. make a service for this
		if (history.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User has no scores");
		}
		return new ResponseEntity<>(history.get(), HttpStatus.OK);
	}

	@DeleteMapping("/{id}")
	public void delete(@PathVariable Long id) {
		scoreRepository.deleteById(id);
	}
}
