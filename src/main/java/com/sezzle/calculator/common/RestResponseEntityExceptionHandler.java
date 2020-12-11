package com.sezzle.calculator.common;

import com.sezzle.calculator.Severity;
import com.sezzle.calculator.exception.BackToFutureException;
import com.sezzle.calculator.exception.InvalidQuestionAnswerException;
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
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        return handleExceptionInternal(ex,
                new ApiError("Invalid Data", Severity.FATAL, HttpStatus.BAD_REQUEST), headers, HttpStatus.BAD_REQUEST, request);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        return handleExceptionInternal(ex, new ApiError(ex.getBindingResult().toString(), Severity.ERROR, HttpStatus.BAD_REQUEST), headers, HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(value = {InvalidQuestionAnswerException.class})
    protected ResponseEntity<Object> handleInvalidQuestionAnswerException(InvalidQuestionAnswerException ex, HttpHeaders headers, WebRequest request) {
        return handleExceptionInternal(ex, new ApiError(ex.getMessage(), Severity.FATAL, HttpStatus.UNPROCESSABLE_ENTITY), headers, HttpStatus.UNPROCESSABLE_ENTITY, request);
    }

    @ExceptionHandler(value = {BackToFutureException.class})
    protected ResponseEntity<Object> handleBackToFutureException(BackToFutureException ex, HttpHeaders headers, WebRequest request) {
        return handleExceptionInternal(ex, new ApiError(ex.getMessage(), Severity.FATAL, HttpStatus.UNPROCESSABLE_ENTITY), headers, HttpStatus.UNPROCESSABLE_ENTITY, request);
    }
}
