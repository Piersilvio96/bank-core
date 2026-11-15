package it.bank.bankcore.user.domain.repository;

import it.bank.bankcore.user.domain.model.User;

import java.util.Optional;

public interface UserRepository {
    User save(User user);
    Optional<User> findByUuid(String uuid);
    boolean existsByUuid(String uuid);
    boolean existsByFiscalCodeOrEmail(String fiscalCode, String email);
}
