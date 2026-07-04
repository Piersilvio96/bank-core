package it.bank.bankcore.account.domain.repository;

import it.bank.bankcore.account.domain.model.AccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.query.QueryByExampleExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<AccountEntity, Long>, JpaSpecificationExecutor<AccountEntity>, QueryByExampleExecutor<AccountEntity> {
    // You can define custom query methods here if needed

    Optional<AccountEntity> findByUuid(String uuid);

}
