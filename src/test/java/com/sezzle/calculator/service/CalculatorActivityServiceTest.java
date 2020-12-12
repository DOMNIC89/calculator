package com.sezzle.calculator.service;

import com.sezzle.calculator.exception.BackToFutureException;
import com.sezzle.calculator.exception.InvalidQuestionAnswerException;
import com.sezzle.calculator.model.CalculatorActivity;
import com.sezzle.calculator.repository.CalculatorActivityRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;


@ExtendWith(MockitoExtension.class)
class CalculatorActivityServiceTest {

    @Mock
    private CalculatorActivityRepository repository;
    private CalculatorActivityService service;

    @BeforeEach
    public void setup() {
        service = new CalculatorActivityService(repository);
    }


    @Test
    @DisplayName("given activity when valid should save")
    public void testGivenActivityWhenValidShouldSave() throws InvalidQuestionAnswerException, BackToFutureException {
        //prepare
        CalculatorActivity activity = createDummyCalculatorActivity();
        //Action
        service.insert(activity);
        //result
        Mockito.verify(repository).save(activity);
    }

    @Test
    @DisplayName("given activity when invalid with question should throw exception")
    public void testGivenActivityWhenInvalidWithQuestionShouldThrowException() {
        // Prepare
        CalculatorActivity activity = createDummyCalculatorActivity();
        activity.setQuestion("");
        String expectedMessage = "Question field is missing data";
        // Action

        Exception exception = Assertions.assertThrows(InvalidQuestionAnswerException.class, () -> service.insert(activity),
                "Expected to throw InvalidQuestionAnswerException but didn't throw for missing questio field");
        String actualMessage = exception.getMessage();

        Assertions.assertEquals(expectedMessage, actualMessage,
                "InvalidQuestionAnswerException is thrown with a different message for question field");
    }

    @Test
    @DisplayName("given activity when invalid with answer should throw exception")
    public void testInsertWithInvalidAnswer() {
        //Prepare
        CalculatorActivity activity = createDummyCalculatorActivity();
        activity.setAnswer(null);
        String expectedMessage = "Answer field is missing data";

        Exception exception = Assertions.assertThrows(InvalidQuestionAnswerException.class, () -> service.insert(activity),
                "Expected to throw InvalidQuestionAnswerException but didn't throw for missing answer field");
        String actualMessage = exception.getMessage();

        Assertions.assertEquals(expectedMessage, actualMessage,
                "InvalidQuestionAnswerException is thrown with a different message for answer field");
    }

    @Test
    @DisplayName("given activity when timestamp in future should throw exception")
    public void testInsertWithFutureTimestamp() {
        CalculatorActivity activity = createDummyCalculatorActivity();
        activity.setTimestamp(activity.getTimestamp().plusDays(1L));
        String expectedMessage = "Tell me, Future Kid, who's President of the United States in 1985?";

        Exception exception = Assertions.assertThrows(BackToFutureException.class, () -> service.insert(activity),
                "Expected to throw BackToFutureException but was not thrown");
        String actualMessage = exception.getMessage();

        Assertions.assertEquals(expectedMessage, actualMessage, "BackToFutureException was thrown with different message");
    }

    private CalculatorActivity createDummyCalculatorActivity() {
        return new CalculatorActivity("user-1", "2+2", "4", LocalDate.now());
    }


}