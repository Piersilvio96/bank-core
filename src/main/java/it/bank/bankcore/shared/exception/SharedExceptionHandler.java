package it.bank.bankcore.shared.exception;

import it.bank.bankcore.account.api.request.CreateAccountRequest;
import it.bank.bankcore.account.api.response.CreateAccountResponse;
import it.bank.bankcore.shared.api.ErrorResponse;
import it.bank.bankcore.shared.api.FieldErrorResponse;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.ValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import java.util.List;

@RestControllerAdvice
public class SharedExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException exception
    ) {
        List<FieldErrorResponse> errors = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(this::mapFieldError)
                .toList();

        ErrorResponse errorResponse = ErrorResponse.builder()
                .message("Validation failed")
                .details("One or more fields are invalid.")
                .errors(errors)
                .build();

        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolationException(
            ConstraintViolationException exception
    ) {
        List<FieldErrorResponse> errors = exception.getConstraintViolations()
                .stream()
                .map(violation -> new FieldErrorResponse(
                        violation.getPropertyPath().toString(),
                        violation.getMessage(),
                        violation.getInvalidValue()
                ))
                .toList();

        ErrorResponse errorResponse = ErrorResponse.builder()
                .message("Validation failed")
                .details("One or more parameters are invalid.")
                .errors(errors)
                .build();

        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<ErrorResponse> handleHandlerMethodValidationException(
            HandlerMethodValidationException exception
    ) {
        List<FieldErrorResponse> errors = exception.getAllErrors()
                .stream()
                .map(error -> new FieldErrorResponse(
                        null,
                        error.getDefaultMessage(),
                        null
                ))
                .toList();

        ErrorResponse errorResponse = ErrorResponse.builder()
                .message("Validation failed")
                .details("One or more method parameters are invalid.")
                .errors(errors)
                .build();

        return ResponseEntity.badRequest().body(errorResponse);
    }

    private FieldErrorResponse mapFieldError(FieldError fieldError) {
        return new FieldErrorResponse(
                fieldError.getField(),
                fieldError.getDefaultMessage(),
                fieldError.getRejectedValue()
        );
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ErrorResponse> handleException(Exception exception) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setMessage(exception.getMessage());
        errorResponse.setDetails("An error occurred while processing the request.");
        return ResponseEntity.internalServerError().body(errorResponse);
    }

}
