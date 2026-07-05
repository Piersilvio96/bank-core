package it.bank.bankcore.account.application.mapper;

import it.bank.bankcore.account.application.command.CreateAccountCommand;
import it.bank.bankcore.account.application.result.CreateAccountResult;
import it.bank.bankcore.account.application.result.GetAccountBalanceResult;
import it.bank.bankcore.account.application.result.GetAccountResult;
import it.bank.bankcore.account.domain.mapper.AccountDomainMapper;
import it.bank.bankcore.account.domain.model.Account;
import it.bank.bankcore.shared.application.ApplicationMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AccountApplicationMapper implements ApplicationMapper<CreateAccountCommand, Account, CreateAccountResult> {

	private final AccountDomainMapper accountDomainMapper;

	@Override
	public Account toDomain(CreateAccountCommand command) {
		return accountDomainMapper.toDomain(command);
	}

	@Override
	public CreateAccountResult toResult(Account domain) {
		return new CreateAccountResult(
				domain.getUuid(),
				domain.getFirstName(),
				domain.getLastName(),
				domain.getEmail(),
				domain.getFiscalCode(),
				domain.getPhoneNumber(),
				domain.getCity(),
				domain.getState(),
				domain.getCountry(),
				domain.getBalance(),
				domain.getCurrency()
		);
	}

	public GetAccountResult toGetAccountResult(Account domain) {
		return new GetAccountResult(
				domain.getUuid(),
				domain.getFirstName(),
				domain.getLastName(),
				domain.getEmail(),
				domain.getFiscalCode(),
				domain.getPhoneNumber(),
				domain.getCity(),
				domain.getState(),
				domain.getCountry(),
				domain.getStatus()
		);
	}

	public GetAccountBalanceResult toGetAccountBalanceResult(Account domain) {
		return new GetAccountBalanceResult(domain.getBalance(), domain.getCurrency());
	}
}
