package com.sezzle.calculator.service;

import com.sezzle.calculator.command.CalculatorActivityCO;
import com.sezzle.calculator.configuration.MqttServices;
import com.sezzle.calculator.exception.BackToFutureException;
import com.sezzle.calculator.exception.InvalidQuestionAnswerException;
import com.sezzle.calculator.model.CalculatorActivity;
import com.sezzle.calculator.repository.CalculatorActivityRepository;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.commons.util.ReflectionUtils;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;


@ExtendWith(MockitoExtension.class)
class CalculatorActivityServiceTest {

    @Mock
    private CalculatorActivityRepository repository;

    @Mock
    private MqttServices mqttServices;

    private CalculatorActivityService service;

    @BeforeEach
    public void setup() {
        service = new CalculatorActivityService(repository, mqttServices);
    }


    @Test
    @DisplayName("given activity when valid should save")
    public void testGivenActivityWhenValidShouldSave() throws InvalidQuestionAnswerException, BackToFutureException, IOException, MqttException {
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

    @Test
    @DisplayName("given a request for last x mins when present it should return the x elements")
    public void testFindLastXActivitiesLastXMins() {
        //prepare create 5 elements
        CalculatorActivity calculatorActivity1 = createDummyCalculatorActivity(1, 1L);
        CalculatorActivity calculatorActivity2 = createDummyCalculatorActivity(2, 3L);
        CalculatorActivity calculatorActivity3 = createDummyCalculatorActivity(3, 7L);
        CalculatorActivity calculatorActivity4 = createDummyCalculatorActivity(4, 9L);
        CalculatorActivity calculatorActivity5 = createDummyCalculatorActivity(5, 9L);

        CalculatorActivityCO activityCO1 = createDummyCalculatorActivityCO(1,1L);
        activityCO1.setTimestamp(calculatorActivity1.getTimestamp());
        CalculatorActivityCO activityCO2 = createDummyCalculatorActivityCO(2, 3L);
        activityCO2.setTimestamp(calculatorActivity2.getTimestamp());
        CalculatorActivityCO activityCO3 = createDummyCalculatorActivityCO(3,7L);
        activityCO3.setTimestamp(calculatorActivity3.getTimestamp());
        CalculatorActivityCO activityCO4 = createDummyCalculatorActivityCO(4,9L);
        activityCO4.setTimestamp(calculatorActivity4.getTimestamp());
        CalculatorActivityCO activityCO5 = createDummyCalculatorActivityCO(5,9L);
        activityCO5.setTimestamp(calculatorActivity5.getTimestamp());

        LocalDateTime endTime = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        Mockito.when(repository.getAllCalculatorActivityBefore(endTime))
                .thenReturn(Arrays.asList(calculatorActivity1, calculatorActivity2, calculatorActivity3, calculatorActivity4, calculatorActivity5));
        ReflectionTestUtils.setField(service, "lastElements", 3L);
        ReflectionTestUtils.setField(service, "lastMins", 10L);

        List<CalculatorActivityCO> activityCOList = service.findLastXActivitiesLastXMins(endTime);

        Assertions.assertEquals(3, activityCOList.size());
        Assertions.assertFalse(activityCOList.contains(activityCO4));
        Assertions.assertFalse(activityCOList.contains(activityCO5));
    }

    @Test
    @DisplayName("given a request for last x mins when present it should return the x elements")
    public void testFindLast3ActivitiesLastXMins() {
        //prepare create 5 elements
        CalculatorActivity calculatorActivity1 = createDummyCalculatorActivity(1, 1L);
        CalculatorActivity calculatorActivity2 = createDummyCalculatorActivity(2, 3L);
        CalculatorActivity calculatorActivity3 = createDummyCalculatorActivity(3, 7L);
        CalculatorActivity calculatorActivity4 = createDummyCalculatorActivity(4, 9L);
        CalculatorActivity calculatorActivity5 = createDummyCalculatorActivity(5, 9L);

        CalculatorActivityCO activityCO1 = createDummyCalculatorActivityCO(1,1L);
        activityCO1.setTimestamp(calculatorActivity1.getTimestamp());
        CalculatorActivityCO activityCO2 = createDummyCalculatorActivityCO(2, 3L);
        activityCO2.setTimestamp(calculatorActivity2.getTimestamp());
        CalculatorActivityCO activityCO3 = createDummyCalculatorActivityCO(3,7L);
        activityCO3.setTimestamp(calculatorActivity3.getTimestamp());
        CalculatorActivityCO activityCO4 = createDummyCalculatorActivityCO(4,9L);
        activityCO4.setTimestamp(calculatorActivity4.getTimestamp());
        CalculatorActivityCO activityCO5 = createDummyCalculatorActivityCO(5,9L);
        activityCO5.setTimestamp(calculatorActivity5.getTimestamp());

        LocalDateTime endTime = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        Mockito.when(repository.getAllCalculatorActivityBefore(endTime))
                .thenReturn(Arrays.asList(calculatorActivity1, calculatorActivity2, calculatorActivity3, calculatorActivity4, calculatorActivity5));
        ReflectionTestUtils.setField(service, "lastElements", 3L);
        ReflectionTestUtils.setField(service, "lastMins", 7L);

        List<CalculatorActivityCO> activityCOList = service.findLastXActivitiesLastXMins(endTime);

        Assertions.assertEquals(2, activityCOList.size());
        Assertions.assertFalse(activityCOList.contains(activityCO4));
        Assertions.assertFalse(activityCOList.contains(activityCO5));
    }

    private CalculatorActivity createDummyCalculatorActivity() {
        return new CalculatorActivity("user-1", "2+2", "4", LocalDateTime.now());
    }

    private CalculatorActivity createDummyCalculatorActivity(int id, long timeInDifference) {
        return new CalculatorActivity("user-"+id, "2+2", "4", LocalDateTime.now().minusMinutes(timeInDifference));
    }

    private CalculatorActivityCO createDummyCalculatorActivityCO(int id, long timeMinuteDifference) {
        return new CalculatorActivityCO("user-"+id, "2+2", "4", LocalDateTime.now().minusMinutes(timeMinuteDifference));
    }

}