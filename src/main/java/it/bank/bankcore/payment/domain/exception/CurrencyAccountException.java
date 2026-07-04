package it.bank.bankcore.payment.domain.exception;

import it.bank.bankcore.shared.exception.BusinessRuleConstraintException;
import lombok.Getter;

@Getter
public class CurrencyAccountException extends BusinessRuleConstraintException {

    /**
     * Creates a new CurrencyAccountException.
     *
     * @param accountCurrency is the currency of the account
     * @param paymentCurrency is the currency of the failed payment
     */
    public CurrencyAccountException(String accountCurrency, String paymentCurrency) {
        super("Currency mismatch: account=" + accountCurrency + " vs payment=" + paymentCurrency);
    }

}
