package it.bank.bankcore.account.domain.exception;

import it.bank.bankcore.shared.exception.ErrorCode;
import it.bank.bankcore.shared.exception.BusinessRuleInputException;

public class InsufficientFundsException extends BusinessRuleInputException {
    public InsufficientFundsException(String string) {
        super(ErrorCode.INSUFFICIENT_FUNDS, string);
    }
}
