package com.springyscores;

import com.springyscores.models.ScoreModel;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.repository.CrudRepository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface ScoreRepository extends CrudRepository<ScoreModel, Long> {
    Optional<List<ScoreModel>> findByPlayerOrderByScore(String player);

    Slice<ScoreModel> findByPlayerIn(List<String> players, Pageable pageable);

    Slice<ScoreModel> findByTimeAfterAndTimeBefore(Date after, Date before, Pageable pageable);

    Slice<ScoreModel> findByPlayerInAndTimeAfterAndTimeBefore(List<String> players, Date after, Date before, Pageable pageable);

    Slice<ScoreModel> findByPlayerInAndTimeBefore(List<String> players, Date before, Pageable pageable);

    Slice<ScoreModel> findByTimeBefore(Date before, Pageable pageable);

    Slice<ScoreModel> findByPlayerInAndTimeAfter(List<String> players, Date before, Pageable pageable);

    Slice<ScoreModel> findByTimeAfter(Date before, Pageable pageable);
}
