package it.bank.bankcore.payment.domain.model;

import it.bank.bankcore.payment.domain.enums.PaymentStatus;
import it.bank.bankcore.payment.domain.exception.PaymentStatusInvalid;
import it.bank.bankcore.shared.domain.Base;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.util.Objects;

@Getter
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class Payment extends Base {

    private String sourceAccountUuid;
    private String targetAccountUuid;
    private BigDecimal amount;
    private String reason;
    private PaymentStatus status;
    private String currency;
    private String requestCode;
    private Payment reversedPayment;
    private Payment reversalPayment;

    public void complete() {
        this.status = PaymentStatus.COMPLETED;
    }

    public void revert(Payment paymentToBeReverted) {
        if (!Objects.equals(paymentToBeReverted.getStatus(), PaymentStatus.COMPLETED)) {
            throw new PaymentStatusInvalid("Payment status must be COMPLETED");
        }
        this.reversedPayment = paymentToBeReverted;
        paymentToBeReverted.reversalPayment = this;
        paymentToBeReverted.status = PaymentStatus.REVERSED;
    }

}
