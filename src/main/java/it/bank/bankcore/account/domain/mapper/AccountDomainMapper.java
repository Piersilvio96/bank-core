package it.bank.bankcore.account.domain.mapper;

import it.bank.bankcore.account.application.command.CreateAccountCommand;
import it.bank.bankcore.account.domain.enums.AccountStatus;
import it.bank.bankcore.account.domain.model.Account;
import it.bank.bankcore.shared.domain.DomainMapper;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class AccountDomainMapper implements DomainMapper<CreateAccountCommand, Account> {

	@Override
	public Account toDomain(CreateAccountCommand command) {
		var currency = (command.currency() == null || command.currency().isBlank()) ? "EUR" : command.currency().toUpperCase();

		return Account.builder()
				.userUuid(command.userUuid())
				.balance(BigDecimal.ZERO)
				.currency(currency)
				.status(AccountStatus.ACTIVE)
				.build();
	}
}
