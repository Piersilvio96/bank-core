package it.bank.bankcore.payment.api.request;

import jakarta.validation.constraints.NotBlank;

public record ReversalRequest(
        @NotBlank(message = "Payment ID cannot be blank")
        String paymentId,
        String reason,
        @NotBlank(message = "Request code cannot be blank")
        String requestCode
) {
}
