package it.bank.bankcore.payment.application.validation;

import it.bank.bankcore.account.domain.enums.AccountStatus;
import it.bank.bankcore.account.domain.exception.AccountNotFoundException;
import it.bank.bankcore.account.domain.exception.AccountStatusException;
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
        var sourceAccount = accountRepository.findByUuidForUpdate(input.sourceAccountUuid())
                .orElseThrow(() -> new AccountNotFoundException(input.sourceAccountUuid()));
        var targetAccount = accountRepository.findByUuidForUpdate(input.targetAccountUuid())
                .orElseThrow(() -> new AccountNotFoundException(input.targetAccountUuid()));


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

        if (ObjectUtils.nullSafeEquals(sourceAccount.getUuid(), targetAccount.getUuid())) {
            throw new SameAccountTransferException(sourceAccount.getUuid());
        }

        return new TransferValidationResult(sourceAccount, targetAccount);
    }

}
