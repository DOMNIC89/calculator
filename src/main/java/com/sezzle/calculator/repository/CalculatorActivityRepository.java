package com.sezzle.calculator.repository;

import com.sezzle.calculator.model.CalculatorActivity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CalculatorActivityRepository extends JpaRepository<CalculatorActivity, String> {
}
