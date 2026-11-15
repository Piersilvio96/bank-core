package it.bank.bankcore.account.domain.repository;

import it.bank.bankcore.account.domain.model.Account;

import java.util.List;
import java.util.Optional;

public interface AccountRepository {
    Account save(Account account);
    Optional<Account> findByUuid(String uuid);
    Optional<Account> findByUuidForUpdate(String uuid);
    List<Account> findAllByUserUuid(String userUuid);
}
