package com.sezzle.calculator.service;

import com.sezzle.calculator.command.CalculatorActivityCO;
import com.sezzle.calculator.configuration.MqttServices;
import com.sezzle.calculator.exception.BackToFutureException;
import com.sezzle.calculator.exception.InvalidArithmeticExpressionException;
import com.sezzle.calculator.exception.InvalidQuestionException;
import com.sezzle.calculator.model.CalculatorActivity;
import com.sezzle.calculator.repository.CalculatorActivityRepository;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;


@ExtendWith(MockitoExtension.class)
class CalculatorActivityServiceTest {

    @Mock
    private CalculatorActivityRepository repository;

    @Mock
    private MqttServices mqttServices;

    private CalculatorActivityService service;

    @BeforeEach
    public void setup() {
        service = new CalculatorActivityService(repository, mqttServices, 3L);
    }

    @Test
    public void testPostConstructInitializeCache() {
        CalculatorActivity activity1 = createDummyCalculatorActivity(1, 0L);
        CalculatorActivity activity2  = createDummyCalculatorActivity(2, 3L);
        CalculatorActivity activity3  = createDummyCalculatorActivity(3, 2L);
        CalculatorActivity activity4  = createDummyCalculatorActivity(4, 3L);
        LocalDateTime endTime = LocalDateTime.now(Clock.systemUTC()).truncatedTo(ChronoUnit.SECONDS);
        Mockito.when(repository.getAllCalculatorActivityBefore(endTime)).thenReturn(Arrays.asList(activity1, activity2, activity3, activity4));
        ReflectionTestUtils.setField(service, "lastMins", 10L);

        service.initializeCache();
        BlockingQueue<CalculatorActivity> queue = (BlockingQueue<CalculatorActivity>) ReflectionTestUtils.getField(service, "queue");
        assert queue != null;
        Assertions.assertEquals(3, queue.size());
        Assertions.assertTrue(queue.contains(activity1));
        Assertions.assertTrue(queue.contains(activity3));
        Assertions.assertTrue(queue.contains(activity2));
        Assertions.assertFalse(queue.contains(activity4));
    }

    @Test
    public void testPostConstructInitializeCacheWithNoValues() {
        LocalDateTime endTime = LocalDateTime.now(Clock.systemUTC()).truncatedTo(ChronoUnit.SECONDS);
        Mockito.when(repository.getAllCalculatorActivityBefore(endTime)).thenReturn(new ArrayList<>());

        service.initializeCache();
        BlockingQueue<CalculatorActivity> queue = (BlockingQueue<CalculatorActivity>) ReflectionTestUtils.getField(service, "queue");
        assert queue != null;
        Assertions.assertEquals(0, queue.size());
    }


    @Test
    @DisplayName("given activity when valid should save")
    public void testGivenActivityWhenValidShouldSave() throws InvalidQuestionException, BackToFutureException, InvalidArithmeticExpressionException {
        //prepare
        CalculatorActivity activity = createDummyCalculatorActivity();
        Mockito.when(repository.save(activity)).thenReturn(activity);
        //Action
        activity.setAnswer("4.0");
        CalculatorActivityCO expectedActivityCO = createDummyCalculatorActivityCO(1, 0L);
        CalculatorActivityCO actualActivityCO = service.insert(activity);
        BlockingQueue<CalculatorActivity> queue = (BlockingQueue<CalculatorActivity>) ReflectionTestUtils.getField(service, "queue");
        assert queue != null;
        Assertions.assertEquals(1, queue.size());
        //result
        Mockito.verify(repository).save(activity);
        Assertions.assertEquals(expectedActivityCO.getAnswer(), actualActivityCO.getAnswer());
    }

    @Test
    @DisplayName("given activity and when the queue is full it should remove the older activity")
    public void testGivenActivityWhenValidShouldRemoveOlderElements() throws InvalidQuestionException, BackToFutureException, InvalidArithmeticExpressionException {
        CalculatorActivity activity1 = createDummyCalculatorActivity();
        activity1.setId(1L);
        CalculatorActivity activity2  = createDummyCalculatorActivity();
        activity2.setId(2L);
        CalculatorActivity activity3  = createDummyCalculatorActivity();
        activity3.setId(3L);

        List<CalculatorActivity> list = Arrays.asList(activity1, activity2, activity3);
        ArrayBlockingQueue<CalculatorActivity> queue = new ArrayBlockingQueue<>(3,true, list);
        ReflectionTestUtils.setField(service, "queue", queue);

        CalculatorActivity activity = createDummyCalculatorActivity();
        CalculatorActivity repositoryActivity = activity;
        repositoryActivity.setId(4L);
        Mockito.when(repository.save(activity)).thenReturn(repositoryActivity);
        service.insert(activity);

        BlockingQueue<CalculatorActivity> expectedQueue = (BlockingQueue<CalculatorActivity>) ReflectionTestUtils.getField(service, "queue");
        assert expectedQueue != null;
        Assertions.assertTrue(expectedQueue.contains(repositoryActivity));
        Assertions.assertTrue(expectedQueue.contains(activity3));
        Assertions.assertTrue(expectedQueue.contains(activity2));
        Assertions.assertFalse(expectedQueue.contains(activity1));
    }

    @Test
    @DisplayName("given activity when invalid with question should throw exception")
    public void testGivenActivityWhenInvalidWithQuestionShouldThrowException() {
        // Prepare
        CalculatorActivity activity = createDummyCalculatorActivity();
        activity.setQuestion("");
        String expectedMessage = "Question field is missing data";
        // Action

        Exception exception = Assertions.assertThrows(InvalidQuestionException.class, () -> service.insert(activity),
                "Expected to throw InvalidQuestionAnswerException but didn't throw for missing question field");
        String actualMessage = exception.getMessage();

        Assertions.assertEquals(expectedMessage, actualMessage,
                "InvalidQuestionAnswerException is thrown with a different message for question field");
    }

    @Test
    @DisplayName("given an activity when question is not a proper arithmetic expression should throw exception")
    public void testGivenActivityWhenInvalidWithQuestionExprShouldThrowException() {
        CalculatorActivity activity = createDummyCalculatorActivity();
        activity.setQuestion("2//2");
        String expectedMessage = "Question has invalid arithmetic expression";

        InvalidArithmeticExpressionException exception = Assertions.assertThrows(InvalidArithmeticExpressionException.class, () -> service.insert(activity));

        String actualMessage = exception.getMessage();
        Assertions.assertEquals(expectedMessage, actualMessage);

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
        ReflectionTestUtils.setField(service, "queue",
                new ArrayBlockingQueue<>(5, true, Arrays.asList(calculatorActivity1, calculatorActivity2,
                        calculatorActivity3, calculatorActivity4, calculatorActivity5)));
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

        LocalDateTime endTime = LocalDateTime.now(Clock.systemUTC()).truncatedTo(ChronoUnit.SECONDS);
        ReflectionTestUtils.setField(service, "queue",
                new ArrayBlockingQueue<>(5, true, Arrays.asList(calculatorActivity1, calculatorActivity2,
                        calculatorActivity3, calculatorActivity4, calculatorActivity5)));
        ReflectionTestUtils.setField(service, "lastElements", 3L);
        ReflectionTestUtils.setField(service, "lastMins", 7L);

        List<CalculatorActivityCO> activityCOList = service.findLastXActivitiesLastXMins(endTime);

        Assertions.assertEquals(3, activityCOList.size());
        Assertions.assertFalse(activityCOList.contains(activityCO4));
        Assertions.assertFalse(activityCOList.contains(activityCO5));
    }

    @Test
    @DisplayName("given a request for last x activities in last y mins when repository returns empty should return empty COs")
    public void testFindLastXActivitiesLastYMinsWithNoResult() {
        LocalDateTime endTime = LocalDateTime.now(Clock.systemUTC()).truncatedTo(ChronoUnit.SECONDS);
        Mockito.when(repository.getAllCalculatorActivityBefore(endTime)).thenReturn(new ArrayList<>());
        List<CalculatorActivityCO> expectedList = new ArrayList<>();
        List<CalculatorActivityCO> actualList = service.findLastXActivitiesLastXMins(endTime);
        Assertions.assertEquals(expectedList, actualList);
    }

    private CalculatorActivity createDummyCalculatorActivity() {
        return new CalculatorActivity("user-1", "2+2", "4", LocalDateTime.now());
    }

    private CalculatorActivity createDummyCalculatorActivity(int id, long timeInDifference) {
        return new CalculatorActivity("user-"+id, "2+2", "4", LocalDateTime.now().minusMinutes(timeInDifference));
    }

    private CalculatorActivityCO createDummyCalculatorActivityCO(int id, long timeMinuteDifference) {
        return new CalculatorActivityCO("user-"+id, "2+2", "4.0", LocalDateTime.now().minusMinutes(timeMinuteDifference));
    }

}