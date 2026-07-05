package it.bank.bankcore.account.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.query.QueryByExampleExecutor;

import java.util.Optional;

public interface AccountJpaRepository extends JpaRepository<AccountJpaEntity, Long>, JpaSpecificationExecutor<AccountJpaEntity>, QueryByExampleExecutor<AccountJpaEntity> {
    // You can define custom query methods here if needed

    Optional<AccountJpaEntity> findByUuid(String uuid);

}
