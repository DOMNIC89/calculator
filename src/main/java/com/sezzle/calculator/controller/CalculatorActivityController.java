package com.sezzle.calculator.controller;

import com.sezzle.calculator.exception.BackToFutureException;
import com.sezzle.calculator.exception.InvalidQuestionAnswerException;
import com.sezzle.calculator.model.CalculatorActivity;
import com.sezzle.calculator.service.CalculatorActivityService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import static com.sezzle.calculator.Constants.API_PATH_V1;
import static com.sezzle.calculator.Constants.CALCULATOR_ACTIVITY_PATH;

@RestController
@RequestMapping(API_PATH_V1 + CALCULATOR_ACTIVITY_PATH)
public class CalculatorActivityController {

    private final CalculatorActivityService service;

    public CalculatorActivityController(CalculatorActivityService service) {
        this.service = service;
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<?> postCalculatorActivity(@RequestBody CalculatorActivity activity) throws InvalidQuestionAnswerException, BackToFutureException {
        service.insert(activity);
        return ResponseEntity.ok().build();
    }
}
