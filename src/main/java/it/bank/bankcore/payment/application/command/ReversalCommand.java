package it.bank.bankcore.payment.application.command;

public record ReversalCommand(
        String paymentId,
        String reason,
        String requestCode
) {
}
