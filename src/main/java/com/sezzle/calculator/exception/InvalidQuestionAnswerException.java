package com.sezzle.calculator.exception;

public class InvalidQuestionAnswerException extends Exception {

    public InvalidQuestionAnswerException(String invalidField) {
        super(String.format("%s field is missing data", invalidField));
    }
}
