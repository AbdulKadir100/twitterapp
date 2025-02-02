package com.kadir.abdul.Twitter_App.exception;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import com.kadir.abdul.Twitter_App.response.ApiResponse;
import com.kadir.abdul.Twitter_App.utils.MessageUtil;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));

        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    /**
     * Exception handler for handling generic exceptions.
     *
     * @param exception The generic Exception that occurred.
     * @return A Future containing a ResponseEntity with details of the exception.
     */
    @ExceptionHandler(Exception.class)
    public CompletableFuture<ResponseEntity<ApiResponse<String>>> handlerGlobalError(Exception exception) {
        return exceptionMessage(exception, HttpStatus.INTERNAL_SERVER_ERROR.value(), MessageUtil.INTERNAL_ERROR);
    }

    /**
     * Constructs a ResponseEntity containing details of the exception.
     *
     * @param exception    The Exception that occurred.
     * @param statusCode   The HTTP status code to be set in the ResponseEntity.
     * @param errorMessage The error message to be included in the response.
     * @param <T>          The type of the error message.
     * @return A Future containing a ResponseEntity with details of the exception.
     */
    private static <T> CompletableFuture<ResponseEntity<ApiResponse<T>>> exceptionMessage(
            Exception exception, int statusCode, T errorMessage) {

        return CompletableFuture.completedFuture(
                ResponseEntity.status(statusCode)
                        .body(new ApiResponse<T>(MessageUtil.FAIL, statusCode, errorMessage)));
    }

}
