package it.bank.bankcore.user.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserJpaRepository extends JpaRepository<UserJpaEntity, Long> {
    Optional<UserJpaEntity> findByUuid(String uuid);
    boolean existsByUuid(String uuid);
    boolean existsByFiscalCodeOrEmail(String fiscalCode, String email);
}
