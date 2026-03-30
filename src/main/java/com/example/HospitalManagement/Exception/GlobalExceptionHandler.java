package com.example.HospitalManagement.Exception;

import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.rest.core.RepositoryConstraintViolationException;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleResourceNotFound(ResourceNotFoundException ex) {
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND,
                ex.getMessage() != null ? ex.getMessage() : "Resource not found");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(detail);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ProblemDetail> handleConstraintViolation(ConstraintViolationException ex) {
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST,
                "Validation failed");
        detail.setProperty("cause", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(detail);
    }

    @ExceptionHandler(TransactionSystemException.class)
    public ResponseEntity<ProblemDetail> handleTransactionSystem(TransactionSystemException ex) {
        Throwable root = ex.getRootCause();
        if (root instanceof ConstraintViolationException cve) {
            return handleConstraintViolation(cve);
        }
        if (containsManualIdentifierMessage(ex)) {
            return roomNumberRequiredResponse(ex);
        }
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR,
                "Transaction failed");
        detail.setProperty("cause", ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(detail);
    }

    @ExceptionHandler(JpaSystemException.class)
    public ResponseEntity<ProblemDetail> handleJpaSystem(JpaSystemException ex) {
        if (containsManualIdentifierMessage(ex)) {
            return roomNumberRequiredResponse(ex);
        }
        String message = ex.getMostSpecificCause().getMessage();
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR,
                "Persistence error");
        detail.setProperty("cause", message);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(detail);
    }

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

    private static ResponseEntity<ProblemDetail> roomNumberRequiredResponse(Throwable ex) {
        String message = firstMessageContaining(ex, "manually assigned");
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST,
                "Room number is required before persisting a new room");
        detail.setProperty("cause", message);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(detail);
    }

    private static boolean containsManualIdentifierMessage(Throwable ex) {
        return firstMessageContaining(ex, "manually assigned") != null;
    }

    private static String firstMessageContaining(Throwable ex, String fragment) {
        for (Throwable t = ex; t != null; t = t.getCause()) {
            String m = t.getMessage();
            if (m != null && m.contains(fragment)) {
                return m;
            }
        }
        return null;
    }

    @ExceptionHandler(CertificationNotFoundException.class)
public ResponseEntity<ProblemDetail> handleCertificationNotFound(
        CertificationNotFoundException ex) {
    ProblemDetail detail = ProblemDetail.forStatusAndDetail(
            HttpStatus.NOT_FOUND, ex.getMessage());
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(detail);
}

@ExceptionHandler(RepositoryConstraintViolationException.class)
public ResponseEntity<ProblemDetail> handleRepositoryConstraintViolation(
        RepositoryConstraintViolationException ex) {
    ProblemDetail detail = ProblemDetail.forStatusAndDetail(
            HttpStatus.BAD_REQUEST, "Validation failed");
    detail.setProperty("cause", ex.getErrors().getFieldErrors()
            .stream()
            .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
            .toList());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(detail);
}


}
