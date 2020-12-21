package com.sezzle.calculator.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sezzle.calculator.Constants;
import com.sezzle.calculator.Severity;
import com.sezzle.calculator.command.CalculatorActivityCO;
import com.sezzle.calculator.common.ApiError;
import com.sezzle.calculator.model.CalculatorActivity;
import com.sezzle.calculator.repository.CalculatorActivityRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;

import static com.sezzle.calculator.Constants.API_PATH_V1;
import static com.sezzle.calculator.configuration.WebConfig.OBJECT_MAPPER;
import static io.restassured.RestAssured.expect;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
@TestPropertySource("classpath:application-test.properties")
class CalculatorActivityControllerIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private CalculatorActivityRepository repository;

    @BeforeEach
    public void init() {

    }

    @Test
    public void testPostCalculatorActivity() throws JsonProcessingException {
        CalculatorActivity activity = new CalculatorActivity("userA", "12+2", "14", LocalDateTime.now(Clock.systemUTC()));
        CalculatorActivityCO expectedCO = new CalculatorActivityCO("userA", "12+2", "14.0", LocalDateTime.now(Clock.systemUTC()));
        CalculatorActivityCO actualCO = given()
                .header("content-type", "application/json")
                .with()
                .body(OBJECT_MAPPER.writeValueAsString(activity))
                .post(constructUrl(port, Constants.CALCULATOR_ACTIVITY_PATH))
                .then()
                .log().ifValidationFails()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(CalculatorActivityCO.class);
        Assertions.assertEquals(expectedCO.getAnswer(), actualCO.getAnswer());
    }

    @Test
    public void testPostCalculatorActivityWithInvalid() {
        String json = "{\"question\": \"\", \"answer\": \"\", \"timestamp\": \"2020-12-20T19:20:37.040Z\", \"user\": \"Bob\"}";
        ApiError expectedAPIError = new ApiError("Question field is missing data", Severity.FATAL, HttpStatus.UNPROCESSABLE_ENTITY);
        given()
                .header("content-type", "application/json")
                .with()
                .body(json)
                .post(constructUrl(port, Constants.CALCULATOR_ACTIVITY_PATH))
                .then()
                .log().ifValidationFails()
                .statusCode(HttpStatus.UNPROCESSABLE_ENTITY.value()).body("message", equalTo(expectedAPIError.getMessage()))
                .body("severity", equalTo(expectedAPIError.getSeverity().toString()))
                .body("status", equalTo("UNPROCESSABLE_ENTITY"));

    }

    @Test
    public void testPostCalculatorActivityWithInvalidQuestionExpression() {
        String json = "{\"question\": \"2**8\", \"answer\": \"\", \"timestamp\": \"2020-12-20T19:20:37.040Z\", \"user\": \"Bob\"}";
        ApiError expectedAPIError = new ApiError("Question has invalid arithmetic expression", Severity.FATAL, HttpStatus.UNPROCESSABLE_ENTITY);
        ApiError actualError = given()
                .header("content-type", "application/json")
                .with()
                .body(json)
                .post(constructUrl(port, Constants.CALCULATOR_ACTIVITY_PATH))
                .then()
                .log().ifValidationFails()
                .statusCode(HttpStatus.UNPROCESSABLE_ENTITY.value())
                .extract()
                .as(ApiError.class);
        Assertions.assertEquals(expectedAPIError.getMessage(), actualError.getMessage());
        Assertions.assertEquals(expectedAPIError.getSeverity(), actualError.getSeverity());
        Assertions.assertEquals(expectedAPIError.getStatus(), actualError.getStatus());
    }

    @Test
    public void testFindAllLastXActivities() {
        LocalDateTime now = LocalDateTime.now(Clock.systemUTC());
        CalculatorActivity activity = new CalculatorActivity("Bob", "2+2", "4", now.minusMinutes(2L));
        CalculatorActivity savedActivity = repository.save(activity);
        List actualResponse = given()
                .header("content-type", "application/json")
                .with()
                .get(constructUrl(port, Constants.CALCULATOR_ACTIVITY_PATH))
                .then()
                .log().ifValidationFails()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .body()
                .as(List.class);
        Assertions.assertEquals(1, actualResponse.size());
        repository.delete(savedActivity);
    }

    private String constructUrl(int port, String path) {
        return String.format("http://localhost:%s%s%s", port, API_PATH_V1, path);
    }
}