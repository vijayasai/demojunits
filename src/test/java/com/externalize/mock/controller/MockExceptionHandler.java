package com.externalize.mock.controller;


import com.externalize.mock.exception.MockException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.HttpServletRequest;

@ControllerAdvice
@RestController
public class MockExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(MockException.class)
    public ResponseEntity<String> mockExceptionHandler(HttpServletRequest req, MockException mockException) {

        String errorMessage = mockException.getErrorMessge();
        int httpStatus = mockException.getHttpStatus();
        String responseStr = "{\"Error\" : \"" + errorMessage + "\"}";
        return new ResponseEntity<String>(responseStr, null, HttpStatus.valueOf(httpStatus));
    }
}