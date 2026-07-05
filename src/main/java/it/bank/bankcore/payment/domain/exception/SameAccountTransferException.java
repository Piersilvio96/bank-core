package it.bank.bankcore.payment.domain.exception;

import it.bank.bankcore.shared.exception.BusinessRuleInputException;

public class SameAccountTransferException extends BusinessRuleInputException {

    public SameAccountTransferException(String uuid) {
        super("Source and target accounts must be different: " + uuid);
    }
}
