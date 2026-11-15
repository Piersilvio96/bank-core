package it.bank.bankcore.payment.domain.exception;

import it.bank.bankcore.shared.exception.ErrorCode;
import it.bank.bankcore.shared.exception.NotFoundException;

public class PaymentNotFoundException extends NotFoundException {
    public PaymentNotFoundException(String paymentId) {
        super(ErrorCode.PAYMENT_NOT_FOUND, "Payment with ID " + paymentId + " not found.");
    }
}
