package it.bank.bankcore.payment.infrastructure.persistence;

import it.bank.bankcore.payment.domain.model.Payment;
import it.bank.bankcore.payment.domain.repository.PaymentRepository;
import it.bank.bankcore.payment.infrastructure.persistence.mapper.PaymentJpaMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PaymentRepositoryImpl implements PaymentRepository {

    private final PaymentJpaRepository paymentJpaRepository;
    private final PaymentJpaMapper paymentJpaMapper;

    @Override
    public Payment save(Payment payment) {
        var paymentEntity = paymentJpaMapper.toEntity(payment);
        var savedEntity = paymentJpaRepository.save(paymentEntity);
        return paymentJpaMapper.toDomain(savedEntity);
    }
}

