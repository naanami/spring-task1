package com.epam.gymcrm.exception;

import com.epam.gymcrm.dto.response.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFound(NotFoundException ex) {
        return new ErrorResponse(404, ex.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleIllegalArgument(IllegalArgumentException ex) {
        return new ErrorResponse(400, ex.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleGeneric(RuntimeException ex) {
        return new ErrorResponse(500, "Something went wrong");
    }

    @ExceptionHandler(UserBlockedException.class)
    public ResponseEntity<?> handleUserBlocked(UserBlockedException ex) {
        return ResponseEntity
                .status(423)
                .body(Map.of(
                        "status", 423,
                        "message", ex.getMessage()
                ));
    }
}