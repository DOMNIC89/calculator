package com.sezzle.calculator.controller;

import com.sezzle.calculator.command.CalculatorActivityCO;
import com.sezzle.calculator.exception.BackToFutureException;
import com.sezzle.calculator.exception.InvalidQuestionAnswerException;
import com.sezzle.calculator.model.CalculatorActivity;
import com.sezzle.calculator.service.CalculatorActivityService;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
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

    @Value("${calculator.last-mins}")
    private Long lastMins;

    public CalculatorActivityController(CalculatorActivityService service) {
        this.service = service;
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<?> postCalculatorActivity(@RequestBody CalculatorActivity activity) throws InvalidQuestionAnswerException, BackToFutureException {
        try {
            service.insert(activity);
        } catch (IOException | MqttException e) {
            LOG.error("Caught an exception while broadcasting ", e);
        }
        return ResponseEntity.ok().build();
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<CalculatorActivityCO>> findAllLastXActivities() {
        LocalDateTime endTime = LocalDateTime.now().plusMinutes(2L).truncatedTo(ChronoUnit.SECONDS);
        return ResponseEntity.ok(service.findLastXActivitiesLastXMins(endTime));
    }
}
