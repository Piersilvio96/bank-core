package it.bank.bankcore.shared.api;

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
    private String message;
    private String details;
    @Builder.Default
    private Instant timestamp = Instant.now();
    private List<FieldErrorResponse> errors;
}