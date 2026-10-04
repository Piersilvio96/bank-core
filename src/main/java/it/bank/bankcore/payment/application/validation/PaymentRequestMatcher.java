package it.bank.bankcore.payment.application.validation;

import it.bank.bankcore.payment.application.command.DepositCommand;
import it.bank.bankcore.payment.application.command.WithdrawCommand;
import it.bank.bankcore.payment.application.command.TransferCommand;
import it.bank.bankcore.payment.application.command.ReversalCommand;
import it.bank.bankcore.payment.domain.enums.PaymentType;
import it.bank.bankcore.payment.domain.exception.RequestCodeConflictException;
import it.bank.bankcore.payment.domain.model.Payment;

import java.math.BigDecimal;
import java.util.Objects;

public final class PaymentRequestMatcher {
    private PaymentRequestMatcher() {}

    public static Payment match(Payment payment, DepositCommand command) {
        check(payment.getType() == PaymentType.DEPOSIT && payment.getSourceAccountUuid() == null
                && Objects.equals(payment.getTargetAccountUuid(), command.accountUuid())
                && amount(payment.getAmount(), command.amount()) && Objects.equals(payment.getCurrency(), command.currency()));
        return payment;
    }

    public static Payment match(Payment payment, WithdrawCommand command) {
        check(payment.getType() == PaymentType.WITHDRAW && payment.getSourceAccountUuid() == null
                && Objects.equals(payment.getTargetAccountUuid(), command.accountUuid())
                && amount(payment.getAmount(), command.amount()) && Objects.equals(payment.getCurrency(), command.currency()));
        return payment;
    }

    public static Payment match(Payment payment, TransferCommand command) {
        check(payment.getType() == PaymentType.TRANSFER
                && Objects.equals(payment.getSourceAccountUuid(), command.sourceAccountUuid())
                && Objects.equals(payment.getTargetAccountUuid(), command.targetAccountUuid())
                && amount(payment.getAmount(), command.amount()) && Objects.equals(payment.getCurrency(), command.currency())
                && Objects.equals(normalize(payment.getReason(), "Transfer"), normalize(command.reason(), "Transfer")));
        return payment;
    }

    public static Payment match(Payment payment, ReversalCommand command) {
        check(payment.getType() == PaymentType.REVERSAL && payment.getReversedPayment() != null
                && Objects.equals(payment.getReversedPayment().getUuid(), command.paymentId())
                && Objects.equals(normalize(payment.getReason(), null), normalize(command.reason(), null)));
        return payment;
    }

    private static boolean amount(BigDecimal first, BigDecimal second) {
        return first != null && second != null && first.compareTo(second) == 0;
    }

    private static String normalize(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value.trim();
    }

    private static void check(boolean matches) {
        if (!matches) throw new RequestCodeConflictException();
    }
}
