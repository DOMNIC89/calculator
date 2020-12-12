package com.sezzle.calculator.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sezzle.calculator.Constants;
import com.sezzle.calculator.Severity;
import com.sezzle.calculator.common.ApiError;
import com.sezzle.calculator.model.CalculatorActivity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;

import static com.sezzle.calculator.Constants.API_PATH_V1;
import static com.sezzle.calculator.configuration.WebConfig.OBJECT_MAPPER;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
@TestPropertySource("classpath:application-test.properties")
class CalculatorActivityControllerIntegrationTest {

    @LocalServerPort
    private int port;

    @BeforeEach
    public void init() {

    }

    @Test
    public void testPostCalculatorActivity() throws JsonProcessingException {
        CalculatorActivity activity = new CalculatorActivity("userA", "12+2", "14", LocalDate.now());

        given()
                .header("content-type", "application/json")
                .with()
                .body(OBJECT_MAPPER.writeValueAsString(activity))
                .post(constructUrl(port, Constants.CALCULATOR_ACTIVITY_PATH))
                .then()
                .log().ifValidationFails()
                .statusCode(HttpStatus.OK.value());
    }

    @Test
    public void testPostCalculatorActivityWithInvalid() {
        String json = String.format("{\"question\": \"2+2\", \"answer\": \"\", \"timestamp\": \"%s\", \"user\": \"Bob\"}", LocalDate.now().toString());
        ApiError expectedAPIError = new ApiError("Answer field is missing data", Severity.FATAL, HttpStatus.UNPROCESSABLE_ENTITY);
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

    private String constructUrl(int port, String path) {
        return String.format("http://localhost:%s%s%s", port, API_PATH_V1, path);
    }
}