package com.sezzle.calculator.controller;

import com.sezzle.calculator.command.CalculatorActivityCO;
import com.sezzle.calculator.exception.BackToFutureException;
import com.sezzle.calculator.exception.InvalidArithmeticExpressionException;
import com.sezzle.calculator.exception.InvalidQuestionException;
import com.sezzle.calculator.model.CalculatorActivity;
import com.sezzle.calculator.service.CalculatorActivityService;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CalculatorActivityControllerTest {

    @Mock
    private CalculatorActivityService service;
    private CalculatorActivityController controller;

    @BeforeEach
    void setUp() {
        controller = new CalculatorActivityController(service);
    }

    @Test
    @DisplayName("given an activity when valid should call insert method of service and return status as ok")
    public void testPostCalculatorActivity() throws InvalidQuestionException, BackToFutureException, InvalidArithmeticExpressionException {
        LocalDateTime now = LocalDateTime.now(Clock.systemUTC());
        CalculatorActivity activity = new CalculatorActivity("user-1", "2*2", "", now);
        CalculatorActivityCO expectedCO = new CalculatorActivityCO("user-1", "2*2", "4", now);
        Mockito.when(service.insert(activity)).thenReturn(expectedCO);
        ResponseEntity<CalculatorActivityCO> responseEntity = controller.postCalculatorActivity(activity);
        CalculatorActivityCO actualCO = responseEntity.getBody();
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assert actualCO != null;
        assertEquals(expectedCO.getAnswer(), actualCO.getAnswer());
    }

    @Test
    @DisplayName("given an activity when invalid should throw an exception")
    public void testPostCalculatorActivityThrowInvalidQuestionAnswerException() throws InvalidQuestionException, BackToFutureException, InvalidArithmeticExpressionException {
        CalculatorActivity activity = new CalculatorActivity("user-1", "", "4", LocalDateTime.now());
        Mockito.doThrow(new InvalidQuestionException("Question")).when(service).insert(activity);
        Assertions.assertThrows(InvalidQuestionException.class, () -> controller.postCalculatorActivity(activity),
                "Exception is not thrown as expected");
    }

    @Test
    @DisplayName("given an activity when invalid expression should throw an exception")
    public void testPostCalculatorActivityThrowInvalidArithmeticExpressionException() throws InvalidArithmeticExpressionException, BackToFutureException, InvalidQuestionException {
        CalculatorActivity activity = new CalculatorActivity("user-1", "2++2", "", LocalDateTime.now());
        Mockito.doThrow(new InvalidArithmeticExpressionException()).when(service).insert(activity);
        Assertions.assertThrows(InvalidArithmeticExpressionException.class, () -> controller.postCalculatorActivity(activity));
    }

    @Test
    @DisplayName("given a start time and end time when elements present should return the response with those elements")
    public void testFindAllLastXActivities() {
        CalculatorActivityCO activityCO1 = createDummyCalculatorActivityCO(1, 1L);
        CalculatorActivityCO activityCO2 = createDummyCalculatorActivityCO(2, 5L);
        CalculatorActivityCO activityCO3 = createDummyCalculatorActivityCO(3, 7L);
        List<CalculatorActivityCO> list = Arrays.asList(activityCO1, activityCO2, activityCO3);
        ResponseEntity<List<CalculatorActivityCO>> expectedResponse = ResponseEntity.ok(list);

        LocalDateTime endTime = LocalDateTime.now(Clock.systemUTC()).truncatedTo(ChronoUnit.SECONDS);
        Mockito.when(service.findLastXActivitiesLastXMins(endTime)).thenReturn(list);

        ResponseEntity<List<CalculatorActivityCO>> actualResponse = controller.findAllLastXActivities();
        Assertions.assertEquals(Objects.requireNonNull(expectedResponse.getBody()).size(), Objects.requireNonNull(actualResponse.getBody()).size());
    }

    @Test
    @DisplayName("given a start time and end time when no elements present should return the response with no elements present")
    public void testFindAllLastXActivitiesForNoElements() {
        List<CalculatorActivityCO> list = new ArrayList<>();
        ResponseEntity<List<CalculatorActivityCO>> expectedResponse = ResponseEntity.ok(list);

        LocalDateTime endTime = LocalDateTime.now(Clock.systemUTC()).truncatedTo(ChronoUnit.SECONDS);
        Mockito.when(service.findLastXActivitiesLastXMins(endTime)).thenReturn(list);

        ResponseEntity<List<CalculatorActivityCO>> actualResponse = controller.findAllLastXActivities();
        Assertions.assertEquals(expectedResponse.getBody().size(), actualResponse.getBody().size());
    }


    private CalculatorActivityCO createDummyCalculatorActivityCO(int id, long timeMinuteDifference) {
        return new CalculatorActivityCO("user-"+id, "2+2", "4", LocalDateTime.now().minusMinutes(timeMinuteDifference));
    }
}