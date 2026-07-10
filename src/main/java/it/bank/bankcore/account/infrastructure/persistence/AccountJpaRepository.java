package it.bank.bankcore.account.infrastructure.persistence;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.QueryByExampleExecutor;

import java.util.Optional;

public interface AccountJpaRepository extends JpaRepository<AccountJpaEntity, Long>, JpaSpecificationExecutor<AccountJpaEntity>, QueryByExampleExecutor<AccountJpaEntity> {
    // You can define custom query methods here if needed

    Optional<AccountJpaEntity> findByUuid(String uuid);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT a FROM AccountJpaEntity a WHERE a.uuid = :uuid")
    Optional<AccountJpaEntity> findByUuidForUpdate(String uuid);

    Boolean existsByFiscalCodeOrEmail(String fiscalCode, String email);

}
