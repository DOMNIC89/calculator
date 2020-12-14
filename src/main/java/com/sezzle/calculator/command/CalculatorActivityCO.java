package com.sezzle.calculator.command;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CalculatorActivityCO {

    @JsonProperty
    private String user;

    @JsonProperty
    private String question;

    @JsonProperty
    private String answer;

    @JsonProperty
    private LocalDateTime timestamp;

    public CalculatorActivityCO(String user, String question, String answer, LocalDateTime timestamp) {
        this.user = user;
        this.question = question;
        this.answer = answer;
        this.timestamp = timestamp;
    }

    public CalculatorActivityCO() {}

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
