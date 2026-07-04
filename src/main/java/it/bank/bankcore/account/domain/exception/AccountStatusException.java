package it.bank.bankcore.account.domain.exception;

import it.bank.bankcore.account.domain.enums.AccountStatus;
import it.bank.bankcore.shared.exception.BusinessRuleConstraintException;

public class AccountStatusException extends BusinessRuleConstraintException {

    /**
     * Constructor for AccountStatusException.
     * @param currentStatus is the current status of the account
     * @param expectedStatus is the expected status of the account
     */
    public AccountStatusException(AccountStatus currentStatus, AccountStatus expectedStatus) {
        super("Account status is " + currentStatus + ", but expected " + expectedStatus);
    }
}
