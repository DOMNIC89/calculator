package com.sezzle.calculator.controller;

import com.sezzle.calculator.command.CalculatorActivityCO;
import com.sezzle.calculator.exception.BackToFutureException;
import com.sezzle.calculator.exception.InvalidQuestionAnswerException;
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
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.time.LocalDate;
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
    public void testPostCalculatorActivity() throws InvalidQuestionAnswerException, BackToFutureException, IOException, MqttException {
        CalculatorActivity activity = new CalculatorActivity("user-1", "2+2", "4", LocalDateTime.now());
        ResponseEntity<?> responseEntity = controller.postCalculatorActivity(activity);
        Mockito.verify(service).insert(activity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    @DisplayName("given an activity when invalid should throw an exception")
    public void testPostCalculatorActivityThrowInvalidQuestionAnswerException() throws InvalidQuestionAnswerException, BackToFutureException, IOException, MqttException {
        CalculatorActivity activity = new CalculatorActivity("user-1", "", "4", LocalDateTime.now());
        Mockito.doThrow(new InvalidQuestionAnswerException("Question")).when(service).insert(activity);
        Assertions.assertThrows(InvalidQuestionAnswerException.class, () -> controller.postCalculatorActivity(activity),
                "Exception is not thrown as expected");
    }

    @Test
    @DisplayName("given a start time and end time when elements present should return the response with those elements")
    public void testFindAllLastXActivities() {
        CalculatorActivityCO activityCO1 = createDummyCalculatorActivityCO(1, 1L);
        CalculatorActivityCO activityCO2 = createDummyCalculatorActivityCO(2, 5L);
        CalculatorActivityCO activityCO3 = createDummyCalculatorActivityCO(3, 7L);
        List<CalculatorActivityCO> list = Arrays.asList(activityCO1, activityCO2, activityCO3);
        ResponseEntity<List<CalculatorActivityCO>> expectedResponse = ResponseEntity.ok(list);

        LocalDateTime endTime = LocalDateTime.now().plusMinutes(2L).truncatedTo(ChronoUnit.SECONDS);
        Mockito.when(service.findLastXActivitiesLastXMins(endTime)).thenReturn(list);

        ResponseEntity<List<CalculatorActivityCO>> actualResponse = controller.findAllLastXActivities();
        Assertions.assertEquals(Objects.requireNonNull(expectedResponse.getBody()).size(), Objects.requireNonNull(actualResponse.getBody()).size());
    }

    @Test
    @DisplayName("given a start time and end time when no elements present should return the response with no elements present")
    public void testFindAllLastXActivitiesForNoElements() {
        List<CalculatorActivityCO> list = new ArrayList<>();
        ResponseEntity<List<CalculatorActivityCO>> expectedResponse = ResponseEntity.ok(list);

        LocalDateTime endTime = LocalDateTime.now().plusMinutes(2L).truncatedTo(ChronoUnit.SECONDS);
        Mockito.when(service.findLastXActivitiesLastXMins(endTime)).thenReturn(list);

        ResponseEntity<List<CalculatorActivityCO>> actualResponse = controller.findAllLastXActivities();
        Assertions.assertEquals(expectedResponse.getBody().size(), actualResponse.getBody().size());
    }


    private CalculatorActivityCO createDummyCalculatorActivityCO(int id, long timeMinuteDifference) {
        return new CalculatorActivityCO("user-"+id, "2+2", "4", LocalDateTime.now().minusMinutes(timeMinuteDifference));
    }
}