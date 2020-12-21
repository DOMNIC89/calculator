package com.sezzle.calculator.common;

import com.sezzle.calculator.Severity;
import com.sezzle.calculator.exception.BackToFutureException;
import com.sezzle.calculator.exception.InvalidArithmeticExpressionException;
import com.sezzle.calculator.exception.InvalidQuestionException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(final HttpMessageNotReadableException ex, final HttpHeaders headers, final HttpStatus status, final WebRequest request) {
        return handleExceptionInternal(ex,
                new ApiError("Invalid Data", Severity.FATAL, HttpStatus.BAD_REQUEST), headers, HttpStatus.BAD_REQUEST, request);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(final MethodArgumentNotValidException ex, final HttpHeaders headers, final HttpStatus status, final WebRequest request) {
        return handleExceptionInternal(ex, new ApiError(ex.getBindingResult().toString(), Severity.ERROR, HttpStatus.BAD_REQUEST), headers, HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(value = {InvalidQuestionException.class})
    protected ResponseEntity<Object> handleInvalidQuestionAnswerException(InvalidQuestionException ex, WebRequest request) {
        return handleExceptionInternal(ex, new ApiError(ex.getMessage(), Severity.FATAL, HttpStatus.UNPROCESSABLE_ENTITY), new HttpHeaders(), HttpStatus.UNPROCESSABLE_ENTITY, request);
    }

    @ExceptionHandler(value = {BackToFutureException.class})
    protected ResponseEntity<Object> handleBackToFutureException(final BackToFutureException ex, WebRequest request) {
        return handleExceptionInternal(ex, new ApiError(ex.getMessage(), Severity.FATAL, HttpStatus.UNPROCESSABLE_ENTITY), new HttpHeaders(), HttpStatus.UNPROCESSABLE_ENTITY, request);
    }

    @ExceptionHandler(value = {InvalidArithmeticExpressionException.class})
    protected ResponseEntity<Object> handleInvalidArithmeticExpressionException(final InvalidArithmeticExpressionException ex, WebRequest request) {
        return handleExceptionInternal(ex, new ApiError(ex.getMessage(), Severity.FATAL, HttpStatus.UNPROCESSABLE_ENTITY), new HttpHeaders(), HttpStatus.UNPROCESSABLE_ENTITY, request);
    }
}
