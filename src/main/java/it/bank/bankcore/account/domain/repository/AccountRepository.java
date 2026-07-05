package it.bank.bankcore.account.domain.repository;

import it.bank.bankcore.account.domain.criteria.CreateAccountCriteria;
import it.bank.bankcore.account.domain.model.Account;

import java.util.Optional;

public interface AccountRepository {

    Account save(Account account);

    Optional<Account> findByUuid(String uuid);

    Boolean exists(CreateAccountCriteria criteria);
}
