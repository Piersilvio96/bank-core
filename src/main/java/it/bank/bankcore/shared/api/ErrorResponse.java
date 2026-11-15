package it.bank.bankcore.shared.api;

import it.bank.bankcore.shared.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class ErrorResponse {
    /** Stable machine-readable code: clients branch on this, not on the message. */
    private ErrorCode code;
    /** Human-readable description, meant for logs and debugging rather than for end users. */
    private String message;
    private String details;
    /** Reference of this error occurrence, also written to the server log. */
    private String traceId;
    @Builder.Default
    private Instant timestamp = Instant.now();
    /** Field-level problems, only for VALIDATION_FAILED. */
    private List<FieldErrorResponse> errors;
}
