package it.bank.bankcore.payment.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentJpaRepository extends JpaRepository<PaymentJpaEntity, Long>,
        JpaSpecificationExecutor<PaymentJpaEntity> {

    Optional<PaymentJpaEntity> findByUuid(String uuid);
}
