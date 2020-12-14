package com.sezzle.calculator.repository;

import com.sezzle.calculator.model.CalculatorActivity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class CalculatorActivityRepositoryTest {

    @Autowired
    TestEntityManager testEntityManager;

    @Autowired
    CalculatorActivityRepository repository;

    @Test
    public void testFindAllByTimestampBetweenOrderByTimestampDesc() {
        CalculatorActivity activity1 = createDummyCalculatorActivity(1, 0L);
        testEntityManager.persist(activity1);
        CalculatorActivity activity2 = createDummyCalculatorActivity(2, 5L);
        testEntityManager.persist(activity2);

        LocalDateTime endTime = LocalDateTime.now().plusMinutes(1L).truncatedTo(ChronoUnit.SECONDS);
        List<CalculatorActivity> actualList = repository.getAllCalculatorActivityBefore(endTime);
        assertEquals(2, actualList.size());
    }

    private CalculatorActivity createDummyCalculatorActivity(int id, long timeInDifference) {
        return new CalculatorActivity("user-"+id, "2+2", "4", LocalDateTime.now().minusMinutes(timeInDifference));
    }
}