package com.sezzle.calculator.model;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import org.springframework.lang.NonNull;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.function.Function;

import static com.sezzle.calculator.Constants.DATE_TIME_PATTERN;

@Entity
@Table(name = "CalculatorActivity")
@JsonIgnoreProperties(ignoreUnknown = true)
public class CalculatorActivity implements BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    @JsonIgnore
    private Long id;

    @NonNull
    @JsonProperty
    private String user;

    @NonNull
    @JsonProperty
    private String question;

    @NonNull
    @JsonProperty
    private String answer;

    @NonNull
    @JsonFormat(pattern = DATE_TIME_PATTERN)
    @JsonProperty
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime timestamp;

    public CalculatorActivity(String user, String question, String answer, LocalDateTime timestamp) {
        this.user = user;
        this.question = question;
        this.answer = answer;
        this.timestamp = timestamp;
    }

    public CalculatorActivity() {

    }

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

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}