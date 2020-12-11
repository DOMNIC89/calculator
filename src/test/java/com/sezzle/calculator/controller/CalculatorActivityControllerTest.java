package com.sezzle.calculator.controller;

import com.sezzle.calculator.exception.BackToFutureException;
import com.sezzle.calculator.exception.InvalidQuestionAnswerException;
import com.sezzle.calculator.model.CalculatorActivity;
import com.sezzle.calculator.service.CalculatorActivityService;
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

import java.time.LocalDate;

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
    public void testPostCalculatorActivity() throws InvalidQuestionAnswerException, BackToFutureException {
        CalculatorActivity activity = new CalculatorActivity("id", "user-1", "2+2", "4", LocalDate.now());
        ResponseEntity<?> responseEntity = controller.postCalculatorActivity(activity);
        Mockito.verify(service).insert(activity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    @DisplayName("given an activity when invalid should throw an exception")
    public void testPostCalculatorActivityThrowInvalidQuestionAnswerException() throws InvalidQuestionAnswerException, BackToFutureException {
        CalculatorActivity activity = new CalculatorActivity("id", "user-1", "", "4", LocalDate.now());
        Mockito.doThrow(new InvalidQuestionAnswerException("Question")).when(service).insert(activity);
        Assertions.assertThrows(InvalidQuestionAnswerException.class, () -> controller.postCalculatorActivity(activity),
                "Exception is not thrown as expected");
    }

}