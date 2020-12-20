package com.sezzle.calculator.controller;

import com.sezzle.calculator.command.CalculatorActivityCO;
import com.sezzle.calculator.exception.BackToFutureException;
import com.sezzle.calculator.exception.InvalidQuestionAnswerException;
import com.sezzle.calculator.model.CalculatorActivity;
import com.sezzle.calculator.service.CalculatorActivityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static com.sezzle.calculator.Constants.API_PATH_V1;
import static com.sezzle.calculator.Constants.CALCULATOR_ACTIVITY_PATH;

@RestController
@RequestMapping(API_PATH_V1 + CALCULATOR_ACTIVITY_PATH)
public class CalculatorActivityController {

    private static final Logger LOG = LoggerFactory.getLogger(CalculatorActivityController.class);

    private final CalculatorActivityService service;

    public CalculatorActivityController(CalculatorActivityService service) {
        this.service = service;
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<?> postCalculatorActivity(@RequestBody CalculatorActivity activity) throws InvalidQuestionAnswerException, BackToFutureException {
        LOG.info("New Activity request for user: {}", activity.getUser());
        service.insert(activity);
        return ResponseEntity.ok().build();
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<CalculatorActivityCO>> findAllLastXActivities() {
        LOG.info("Fetching the calculator activities through GET method");
        LocalDateTime endTime = LocalDateTime.now(Clock.systemUTC()).truncatedTo(ChronoUnit.SECONDS);
        return ResponseEntity.ok(service.findLastXActivitiesLastXMins(endTime));
    }
}
