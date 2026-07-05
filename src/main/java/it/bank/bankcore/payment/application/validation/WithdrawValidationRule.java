package it.bank.bankcore.payment.application.validation;

import it.bank.bankcore.account.domain.enums.AccountStatus;
import it.bank.bankcore.account.domain.exception.AccountNotFoundException;
import it.bank.bankcore.account.domain.exception.AccountStatusException;
import it.bank.bankcore.account.domain.repository.AccountRepository;
import it.bank.bankcore.payment.application.command.WithdrawCommand;
import it.bank.bankcore.payment.domain.exception.CurrencyAccountException;
import it.bank.bankcore.shared.application.ValidationRule;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

@Service
@RequiredArgsConstructor
public class WithdrawValidationRule implements ValidationRule<WithdrawCommand> {

    private final AccountRepository accountRepository;

    @Override
    public void validate(WithdrawCommand input) {
        var targetAccount = accountRepository.findByUuid(input.accountUuid())
                .orElseThrow(() -> new AccountNotFoundException(input.accountUuid()));

        if (!ObjectUtils.nullSafeEquals(targetAccount.getStatus(), AccountStatus.ACTIVE)) {
            throw new AccountStatusException(targetAccount.getStatus(), AccountStatus.ACTIVE);
        }

        if (!ObjectUtils.nullSafeEquals(targetAccount.getCurrency(), input.currency())) {
            throw new CurrencyAccountException(targetAccount.getCurrency(), input.currency());
        }

    }

}
