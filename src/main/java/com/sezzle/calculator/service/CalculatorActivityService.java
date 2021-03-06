package com.sezzle.calculator.service;

import com.sezzle.calculator.command.CalculatorActivityCO;
import com.sezzle.calculator.common.CalculatorActivityNapper;
import com.sezzle.calculator.configuration.MqttServices;
import com.sezzle.calculator.exception.BackToFutureException;
import com.sezzle.calculator.exception.InvalidArithmeticExpressionException;
import com.sezzle.calculator.exception.InvalidQuestionException;
import com.sezzle.calculator.helper.ExpressionEvaluation;
import com.sezzle.calculator.helper.StringUtils;
import com.sezzle.calculator.model.CalculatorActivity;
import com.sezzle.calculator.repository.CalculatorActivityRepository;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.stream.Collectors;

@Service
public class CalculatorActivityService {

    private static final Logger LOG = LoggerFactory.getLogger(CalculatorActivityService.class);

    private final CalculatorActivityRepository repository;

    private final MqttServices mqttServices;

    private final ArrayBlockingQueue<CalculatorActivity> queue;

    @Value("${calculator.last-elems}")
    private final Long lastElements;

    @Value("${calculator.last-mins}")
    private Long lastMins;

    public CalculatorActivityService(CalculatorActivityRepository repository, MqttServices mqttServices,
                                     @Value("${calculator.last-elems}") Long lastElements) {
        this.repository = repository;
        this.mqttServices = mqttServices;
        this.lastElements = lastElements;
        this.queue = new ArrayBlockingQueue<>(this.lastElements.intValue());
    }

    @PostConstruct
    public void initializeCache() {
        LocalDateTime endTime = LocalDateTime.now(Clock.systemUTC()).truncatedTo(ChronoUnit.SECONDS);
        List<CalculatorActivity> activities = repository.getAllCalculatorActivityBefore(endTime);
        this.queue.addAll(activities.stream().filter(activity -> activity.getTimestamp().isAfter(LocalDateTime.now().minusMinutes(lastMins)))
                .limit(this.lastElements.intValue()).collect(Collectors.toList()));
    }

    public CalculatorActivityCO insert(CalculatorActivity activity) throws InvalidQuestionException, BackToFutureException, InvalidArithmeticExpressionException {
        if (StringUtils.isEmptyOrNull(activity.getQuestion())) {
            // throw an exception as Invalid Question
            LOG.info("Question field is missing data");
            throw new InvalidQuestionException("Question");
        }

        // check the validity of the question via regex
        if (!ExpressionEvaluation.isValidArithmeticExpression(activity.getQuestion())) {
            LOG.info("Question is an invalid arithmetic expression");
            throw new InvalidArithmeticExpressionException();
        }

        if (LocalDateTime.now().isBefore(activity.getTimestamp())) {
            // throw an exception as BackToFutureException
            LOG.info("Future date is used in the timestamp");
            throw new BackToFutureException("Tell me, Future Kid, who's President of the United States in 1985?");
        }
        // set the answer based on the input received for expression
        activity.setAnswer(String.valueOf(ExpressionEvaluation.evaluate(activity.getQuestion())));

        LOG.info("Saving the activity of user {}", activity.getUser());
        CalculatorActivity savedActivity = repository.save(activity);
        // save the element to the cache
        boolean successfullyAdded = this.queue.offer(savedActivity);
        // if not saved remove the element from the queue and save it on the
        if (!successfullyAdded) {
            this.queue.poll();
            this.queue.offer(savedActivity);
        }
        // broadcast this activity to other users
        LOG.info("Broadcasting the message to all for the activity saved");
        try {
            List<CalculatorActivityCO> activities = this.queue.stream()
                    .map(CalculatorActivityNapper::convertToCalculatorActivityCO)
                    .collect(Collectors.toList());
            mqttServices.sendMessages(activities, savedActivity.getId());
        } catch (IOException | MqttException e) {
            LOG.warn("Unable to send message error: {}", e.getMessage());
        }
        return CalculatorActivityNapper.convertToCalculatorActivityCO(savedActivity);
    }

    // Create a get function to retrieve last 10 minutes of activities
    public List<CalculatorActivityCO> findLastXActivitiesLastXMins(LocalDateTime endTime) {
        LOG.info("Fetching the list for DateTime: {}", endTime);
        if (queue.isEmpty()) {
            List<CalculatorActivity> activities = repository.getAllCalculatorActivityBefore(endTime);
            List<CalculatorActivity> collect = activities.stream().filter(activity -> activity.getTimestamp()
                    .isAfter(LocalDateTime.now(Clock.systemUTC()).minusMinutes(lastMins)))
                    .limit(lastElements).collect(Collectors.toList());
            queue.addAll(collect);
        }
        return queue.stream()
                .filter(activity -> activity.getTimestamp().isAfter(LocalDateTime.now(Clock.systemUTC()).minusMinutes(lastMins)))
                .map(CalculatorActivityNapper::convertToCalculatorActivityCO)
                .limit(lastElements)
                .collect(Collectors.toList());
    }

}
