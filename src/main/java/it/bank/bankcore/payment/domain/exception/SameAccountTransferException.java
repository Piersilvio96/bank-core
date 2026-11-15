package it.bank.bankcore.payment.domain.exception;

import it.bank.bankcore.shared.exception.ErrorCode;
import it.bank.bankcore.shared.exception.BusinessRuleInputException;

public class SameAccountTransferException extends BusinessRuleInputException {

    public SameAccountTransferException(String uuid) {
        super(ErrorCode.SAME_ACCOUNT_TRANSFER, "Source and target accounts must be different: " + uuid);
    }
}
