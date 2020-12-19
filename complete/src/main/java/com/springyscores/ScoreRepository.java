package com.springyscores;

import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface ScoreRepository extends CrudRepository<ScoreModel, Long> {
    Optional<List<ScoreModel>> findByPlayerOrderByScore(String player);
}
