package com.sezzle.calculator.repository;

import com.sezzle.calculator.model.CalculatorActivity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CalculatorActivityRepository extends JpaRepository<CalculatorActivity, Long> {

    List<CalculatorActivity> findAllByTimestampBetween(LocalDateTime timestamp, LocalDateTime timestamp2);

    @Query("select c from CalculatorActivity c where c.timestamp <= :currentDateTime order by c.timestamp desc")
    List<CalculatorActivity> getAllCalculatorActivityBefore(@Param("currentDateTime") LocalDateTime currentDateTime);
}
