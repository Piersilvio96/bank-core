package it.bank.bankcore.shared.exception;

import it.bank.bankcore.shared.api.ErrorResponse;
import it.bank.bankcore.shared.api.FieldErrorResponse;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import java.util.List;
import java.util.UUID;

/**
 * Maps every exception to an {@link ErrorResponse} carrying a stable {@link ErrorCode}
 * and a traceId that is also written to the log.
 */
@Slf4j
@RestControllerAdvice
public class SharedExceptionHandler {

    private static final String GENERIC_DETAILS = "An error occurred while processing the request.";

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ErrorResponse> handleException(NotFoundException exception) {
        return respond(HttpStatus.NOT_FOUND, exception.getCode(), exception.getMessage(), GENERIC_DETAILS, null, exception);
    }

    @ExceptionHandler(BusinessRuleInputException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleException(BusinessRuleInputException exception) {
        return respond(HttpStatus.BAD_REQUEST, exception.getCode(), exception.getMessage(), GENERIC_DETAILS, null, exception);
    }

    @ExceptionHandler(BusinessRuleConstraintException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<ErrorResponse> handleException(BusinessRuleConstraintException exception) {
        return respond(HttpStatus.CONFLICT, exception.getCode(), exception.getMessage(), GENERIC_DETAILS, null, exception);
    }

    @ExceptionHandler(UseCaseException.class)
    public ResponseEntity<ErrorResponse> handleException(UseCaseException exception) {
        return respond(HttpStatus.CONFLICT, ErrorCode.INTERNAL_ERROR, exception.getMessage(), GENERIC_DETAILS, null, exception);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException exception
    ) {
        List<FieldErrorResponse> errors = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(this::mapFieldError)
                .toList();

        return respond(HttpStatus.BAD_REQUEST, ErrorCode.VALIDATION_FAILED, "Validation failed",
                "One or more fields are invalid.", errors, exception);
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

        return respond(HttpStatus.BAD_REQUEST, ErrorCode.VALIDATION_FAILED, "Validation failed",
                "One or more parameters are invalid.", errors, exception);
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

        return respond(HttpStatus.BAD_REQUEST, ErrorCode.VALIDATION_FAILED, "Validation failed",
                "One or more method parameters are invalid.", errors, exception);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingParameter(MissingServletRequestParameterException exception) {
        return respond(HttpStatus.BAD_REQUEST, ErrorCode.INVALID_REQUEST,
                "Missing required parameter: " + exception.getParameterName(), GENERIC_DETAILS, null, exception);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleUnreadableBody(HttpMessageNotReadableException exception) {
        return respond(HttpStatus.BAD_REQUEST, ErrorCode.INVALID_REQUEST,
                "Request body is missing or malformed", GENERIC_DETAILS, null, exception);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ErrorResponse> handleException(Exception exception) {
        // The real cause goes to the log only: internal messages must not leak to clients.
        return respond(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.INTERNAL_ERROR,
                "Unexpected error", GENERIC_DETAILS, null, exception);
    }

    private ResponseEntity<ErrorResponse> respond(
            HttpStatus status,
            ErrorCode code,
            String message,
            String details,
            List<FieldErrorResponse> errors,
            Exception exception
    ) {
        String traceId = UUID.randomUUID().toString();

        if (status.is5xxServerError()) {
            log.error("[{}] {} -> {}", traceId, code, status.value(), exception);
        } else {
            log.warn("[{}] {} -> {}: {}", traceId, code, status.value(), exception.getMessage());
        }

        ErrorResponse errorResponse = ErrorResponse.builder()
                .code(code)
                .message(message)
                .details(details)
                .traceId(traceId)
                .errors(errors)
                .build();

        return ResponseEntity.status(status).body(errorResponse);
    }

    private FieldErrorResponse mapFieldError(FieldError fieldError) {
        return new FieldErrorResponse(
                fieldError.getField(),
                fieldError.getDefaultMessage(),
                fieldError.getRejectedValue()
        );
    }
}
