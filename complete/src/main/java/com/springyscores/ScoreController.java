package com.springyscores;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
public class ScoreController {

	@Autowired
	ScoreRepository scoreRepository;

	@PostMapping(path="/createscore", consumes = "application/json", produces = "application/json")
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	public ScoreModel createScore(@RequestBody ScoreModel scoreModel) {
		scoreRepository.save(scoreModel);
		return scoreModel;
	}

	@GetMapping(path="/{id}")
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	public ScoreModel getSingleScore(@PathVariable Long id) {
		Optional<ScoreModel> model = scoreRepository.findById(id);
		return model.get();
	}
}
