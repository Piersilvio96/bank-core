package it.bank.bankcore.payment.application.validation;

import it.bank.bankcore.account.domain.enums.AccountStatus;
import it.bank.bankcore.account.domain.exception.AccountNotFoundException;
import it.bank.bankcore.account.domain.exception.AccountStatusException;
import it.bank.bankcore.account.domain.model.Account;
import it.bank.bankcore.account.domain.repository.AccountRepository;
import it.bank.bankcore.payment.application.command.TransferCommand;
import it.bank.bankcore.payment.domain.exception.CurrencyAccountException;
import it.bank.bankcore.payment.domain.exception.SameAccountTransferException;
import it.bank.bankcore.shared.application.ValidationRule;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

@Service
@RequiredArgsConstructor
public class TransferValidationRule implements ValidationRule<TransferCommand> {

    private final AccountRepository accountRepository;

    @Override
    public void validate(TransferCommand input) {
        loadAndValidate(input);
    }

    public TransferValidationResult loadAndValidate(TransferCommand input) {
        if (ObjectUtils.nullSafeEquals(input.sourceAccountUuid(), input.targetAccountUuid())) {
            throw new SameAccountTransferException(input.sourceAccountUuid());
        }

        String firstUuid = input.sourceAccountUuid().compareTo(input.targetAccountUuid()) <= 0
                ? input.sourceAccountUuid()
                : input.targetAccountUuid();
        String secondUuid = input.sourceAccountUuid().compareTo(input.targetAccountUuid()) <= 0
                ? input.targetAccountUuid()
                : input.sourceAccountUuid();

        var firstAccount = accountRepository.findByUuidForUpdate(firstUuid).orElse(null);
        var secondAccount = accountRepository.findByUuidForUpdate(secondUuid).orElse(null);

        var sourceAccount = firstUuid.equals(input.sourceAccountUuid()) ? firstAccount : secondAccount;
        var targetAccount = firstUuid.equals(input.sourceAccountUuid()) ? secondAccount : firstAccount;

        if (sourceAccount == null) {
            throw new AccountNotFoundException(input.sourceAccountUuid());
        }

        if (targetAccount == null) {
            throw new AccountNotFoundException(input.targetAccountUuid());
        }

        validateBusinessRules(input, sourceAccount, targetAccount);

        return new TransferValidationResult(sourceAccount, targetAccount);
    }

    private void validateBusinessRules(TransferCommand input, Account sourceAccount, Account targetAccount) {
        if (!ObjectUtils.nullSafeEquals(sourceAccount.getStatus(), AccountStatus.ACTIVE)) {
            throw new AccountStatusException(sourceAccount.getStatus(), AccountStatus.ACTIVE);
        }

        if (!ObjectUtils.nullSafeEquals(targetAccount.getStatus(), AccountStatus.ACTIVE)) {
            throw new AccountStatusException(targetAccount.getStatus(), AccountStatus.ACTIVE);
        }

        if (!ObjectUtils.nullSafeEquals(targetAccount.getCurrency(), input.currency())) {
            throw new CurrencyAccountException(targetAccount.getCurrency(), input.currency());
        }

        if (!ObjectUtils.nullSafeEquals(sourceAccount.getCurrency(), input.currency())) {
            throw new CurrencyAccountException(sourceAccount.getCurrency(), input.currency());
        }
    }

}
