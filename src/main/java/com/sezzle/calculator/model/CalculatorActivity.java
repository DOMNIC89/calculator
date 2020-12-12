package com.sezzle.calculator.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.lang.NonNull;


import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "CalculatorActivity")
@JsonIgnoreProperties(ignoreUnknown = true)
public class CalculatorActivity implements BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    @JsonIgnore
    private Long id;

    @NonNull
    @JsonProperty("user")
    private String user;

    @NonNull
    private String question;

    @NonNull
    private String answer;

    @NonNull
    private LocalDate timestamp;

    public CalculatorActivity(String user, String question, String answer, LocalDate timestamp) {
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

    public LocalDate getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDate timestamp) {
        this.timestamp = timestamp;
    }
}