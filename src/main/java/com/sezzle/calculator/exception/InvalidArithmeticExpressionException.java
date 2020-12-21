package com.sezzle.calculator.exception;

public class InvalidArithmeticExpressionException extends Exception {

    private static final long serialVersionUID = 7280204517164828747L;

    public InvalidArithmeticExpressionException() {
        super("Question has invalid arithmetic expression");
    }
}
