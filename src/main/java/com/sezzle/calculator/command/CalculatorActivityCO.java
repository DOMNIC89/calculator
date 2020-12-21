package com.sezzle.calculator.command;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

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
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
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

    public static class CalculatorActivityCOBuilder {
        private String user;
        private String question;
        private String answer;
        private LocalDateTime timestamp;

        public CalculatorActivityCOBuilder user(String user) {
            this.user = user;
            return this;
        }

        public CalculatorActivityCOBuilder question(String question) {
            this.question = question;
            return this;
        }

        public CalculatorActivityCOBuilder answer(String answer) {
            this.answer = answer;
            return this;
        }

        public CalculatorActivityCOBuilder timestamp(LocalDateTime timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public CalculatorActivityCO build() {
            return new CalculatorActivityCO(this.user, this.question, this.answer, this.timestamp);
        }
    }
}
