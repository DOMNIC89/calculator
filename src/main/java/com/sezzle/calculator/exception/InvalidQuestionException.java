package com.sezzle.calculator.exception;

public class InvalidQuestionException extends Exception {

    private static final long serialVersionUID = -5515903089509864612L;

    public InvalidQuestionException(String invalidField) {
        super(String.format("%s field is missing data", invalidField));
    }
}
