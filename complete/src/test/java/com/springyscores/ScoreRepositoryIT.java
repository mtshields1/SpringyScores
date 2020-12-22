package com.springyscores;

import com.springyscores.models.ScoreModel;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import static org.assertj.core.api.Assertions.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@DataJpaTest
public class ScoreRepositoryIT {

    @Autowired
    TestEntityManager entityManager;

    @Autowired
    private ScoreRepository scoreRepository;

    @Test
    public void whenFindByPlayer_thenReturnInScoreOrder() {
        List<ScoreModel> scores = new ArrayList<>();
        ScoreModel score = new ScoreModel("kurk", 3000, new Date());
        ScoreModel score1 = new ScoreModel("kurk", 1000, new Date());
        ScoreModel score2 = new ScoreModel("kurk", 2000, new Date());
        entityManager.persist(score);
        entityManager.persist(score1);
        entityManager.persist(score2);
        scores.add(score1);
        scores.add(score2);
        scores.add(score);
        entityManager.flush();

        Optional<List<ScoreModel>> found = scoreRepository.findByPlayerOrderByScore("kurk");

        assertThat(found.get()).isEqualTo(scores);
    }
}
