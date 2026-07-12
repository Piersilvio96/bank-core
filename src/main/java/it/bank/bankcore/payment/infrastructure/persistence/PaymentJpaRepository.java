package it.bank.bankcore.payment.infrastructure.persistence;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentJpaRepository extends JpaRepository<PaymentJpaEntity, Long>,
        JpaSpecificationExecutor<PaymentJpaEntity> {

    Optional<PaymentJpaEntity> findByUuid(String uuid);
    Optional<PaymentJpaEntity> findByRequestCode(String requestCode);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query(value = "SELECT p FROM PaymentJpaEntity p WHERE p.uuid = :uuid")
    Optional<PaymentJpaEntity> findByUuidForUpdate(String uuid);
}
