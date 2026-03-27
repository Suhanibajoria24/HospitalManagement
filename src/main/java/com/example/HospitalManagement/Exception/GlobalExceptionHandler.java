package com.example.HospitalManagement.Exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DataIntegrityViolationException.class)
     public ResponseEntity<ProblemDetail> handleDataIntegrity(DataIntegrityViolationException ex) {
         String message = ex.getMostSpecificCause().getMessage();
         if (message != null && message.toLowerCase().contains("duplicate")) {
             ProblemDetail detail = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT,
                     "A record with the same key already exists");
             detail.setProperty("cause", message);
             return ResponseEntity.status(HttpStatus.CONFLICT).body(detail);
         }
         ProblemDetail detail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST,
                 "Data constraint violation");
         detail.setProperty("cause", message);
         return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(detail);
     }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception ex) {
        return ResponseEntity
                .badRequest()
                .body("Invalid input");
}


}
