package it.bank.bankcore.payment.infrastructure.persistence;

import it.bank.bankcore.payment.domain.exception.PaymentNotFoundException;
import it.bank.bankcore.payment.domain.model.Payment;
import it.bank.bankcore.payment.domain.repository.PaymentRepository;
import it.bank.bankcore.payment.infrastructure.exception.PaymentCodeAlreadyExists;
import it.bank.bankcore.payment.infrastructure.persistence.mapper.PaymentJpaMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PaymentRepositoryImpl implements PaymentRepository {

    private final PaymentJpaRepository paymentJpaRepository;
    private final PaymentJpaMapper paymentJpaMapper;

    @Override
    public Payment save(Payment payment) {
        if (paymentJpaRepository.findByRequestCode(payment.getRequestCode()).isPresent()) {
            throw new PaymentCodeAlreadyExists("Payment with request code " + payment.getRequestCode() + " already exists");
        }
        try {
            var paymentEntity = paymentJpaMapper.toEntity(payment);
            var savedEntity = paymentJpaRepository.save(paymentEntity);
            return paymentJpaMapper.toDomain(savedEntity);
        } catch (DataIntegrityViolationException exception) {
            throw new PaymentCodeAlreadyExists("Payment with request code " + payment.getRequestCode() + " already exists");
        }
    }

    @Override
    public Payment updateReversedPayment(Payment payment) {
        var paymentEntity = paymentJpaRepository.findByUuid(payment.getUuid())
                .orElseThrow(() -> new PaymentNotFoundException(payment.getUuid()));

        if (payment.getReversalPayment() != null) {
            var reversalEntity = paymentJpaRepository.findByUuid(payment.getReversalPayment().getUuid())
                    .orElseThrow(() -> new PaymentNotFoundException(payment.getReversalPayment().getUuid()));
            paymentEntity.setReversalPayment(reversalEntity);
            reversalEntity.setReversedPayment(paymentEntity);
            paymentJpaRepository.save(reversalEntity);
        }

        if (payment.getReversedPayment() != null) {
            var reversedEntity = paymentJpaRepository.findByUuid(payment.getReversedPayment().getUuid())
                    .orElseThrow(() -> new PaymentNotFoundException(payment.getReversedPayment().getUuid()));
            paymentEntity.setReversedPayment(reversedEntity);
        }

        paymentEntity.setStatus(payment.getStatus());
        var updatedEntity = paymentJpaRepository.save(paymentEntity);
        return paymentJpaMapper.toDomain(updatedEntity);
    }

    @Override
    public Optional<Payment> findByRequestCode(String requestCode) {
        return paymentJpaRepository.findByRequestCode(requestCode)
                .map(paymentJpaMapper::toDomain);
    }

    @Override
    public Optional<Payment> findByPaymentId(String paymentId) {
        return paymentJpaRepository.findByUuidForUpdate(paymentId)
                .map(paymentJpaMapper::toDomain);
    }
}
