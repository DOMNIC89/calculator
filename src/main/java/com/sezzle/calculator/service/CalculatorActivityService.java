package com.sezzle.calculator.service;

import com.sezzle.calculator.exception.BackToFutureException;
import com.sezzle.calculator.exception.InvalidQuestionAnswerException;
import com.sezzle.calculator.helper.StringUtils;
import com.sezzle.calculator.model.CalculatorActivity;
import com.sezzle.calculator.repository.CalculatorActivityRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class CalculatorActivityService {

    private final CalculatorActivityRepository repository;


    public CalculatorActivityService(CalculatorActivityRepository repository) {
        this.repository = repository;
    }

    public void insert(CalculatorActivity activity) throws InvalidQuestionAnswerException, BackToFutureException {
        if (StringUtils.isEmptyOrNull(activity.getQuestion())) {
            // throw an exception as Invalid Question
            throw new InvalidQuestionAnswerException("Question");
        }

        if (StringUtils.isEmptyOrNull(activity.getAnswer())) {
            // throw an exception as Invalid Answer
            throw new InvalidQuestionAnswerException("Answer");
        }

        if (LocalDate.now().isBefore(activity.getTimestamp())) {
            // throw an exception as BackToFutureException
            throw new BackToFutureException("Tell me, Future Kid, who's President of the United States in 1985?");
        }
        repository.save(activity);
        // broadcast this activity to other users

    }

    // Create a get function to retrieve last 10 minutes of activities
}
