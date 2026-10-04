package it.bank.bankcore.payment.application.usecase;

import it.bank.bankcore.payment.domain.exception.PaymentStatusInvalid;
import it.bank.bankcore.payment.domain.enums.PaymentStatus;
import it.bank.bankcore.account.domain.exception.AccountNotFoundException;
import it.bank.bankcore.account.domain.repository.AccountRepository;
import it.bank.bankcore.ledger.application.command.RecordReversalLedgerCommand;
import it.bank.bankcore.ledger.application.port.LedgerRecorder;
import it.bank.bankcore.payment.application.command.ReversalCommand;
import it.bank.bankcore.payment.application.mapper.PaymentApplicationMapper;
import it.bank.bankcore.payment.application.result.ReversalResult;
import it.bank.bankcore.payment.domain.exception.PaymentNotFoundException;
import it.bank.bankcore.payment.domain.mapper.PaymentDomainMapper;
import it.bank.bankcore.payment.domain.repository.PaymentRepository;
import it.bank.bankcore.payment.infrastructure.exception.PaymentCodeAlreadyExists;
import it.bank.bankcore.shared.application.UseCase;
import it.bank.bankcore.payment.application.service.PaymentExecution;
import it.bank.bankcore.payment.application.validation.PaymentRequestMatcher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

@Service
@RequiredArgsConstructor
public class ReversalUseCase implements UseCase<ReversalCommand, ReversalResult> {

    private final PaymentExecution paymentExecution;
    private final AccountRepository accountRepository;
    private final PaymentRepository paymentRepository;
    private final PaymentDomainMapper paymentDomainMapper;
    private final PaymentApplicationMapper paymentApplicationMapper;
    private final LedgerRecorder ledgerRecorder;

    @Override
    public ReversalResult execute(ReversalCommand input) {
        return paymentExecution.execute(
                () -> paymentRepository.findByRequestCode(input.requestCode())
                        .map(payment -> paymentApplicationMapper.toReversalResult(PaymentRequestMatcher.match(payment, input), false))
                        .orElseGet(() -> processNewReversalCommand(input)),
                () -> getIdempotentReversal(input));
    }

    private ReversalResult processNewReversalCommand(ReversalCommand input) {
        var paymentToBeReversed = paymentRepository.findByPaymentId(input.paymentId())
                .orElseThrow(() -> new PaymentNotFoundException(input.paymentId()));

        // A concurrent reversal may have completed while the original-payment lock was awaited.
        var existingReversal = paymentRepository.findByRequestCode(input.requestCode());
        if (existingReversal.isPresent()) {
            return paymentApplicationMapper.toReversalResult(
                    PaymentRequestMatcher.match(existingReversal.get(), input), false);
        }

        if (paymentToBeReversed.getStatus() != PaymentStatus.COMPLETED
                || paymentToBeReversed.getReversedPayment() != null) {
            throw new PaymentStatusInvalid("Only completed original payments can be reversed");
        }
        var reversalPayment = paymentDomainMapper.toDomain(input, paymentToBeReversed);

        if (!ObjectUtils.isEmpty(reversalPayment.getSourceAccountUuid())){
            var sourceAccount = accountRepository.findByUuidForUpdate(reversalPayment.getSourceAccountUuid())
                    .orElseThrow(() -> new AccountNotFoundException(reversalPayment.getSourceAccountUuid()));
            sourceAccount.withdraw(reversalPayment.getAmount());
            accountRepository.save(sourceAccount);
        }

        if (!ObjectUtils.isEmpty(reversalPayment.getTargetAccountUuid())){
            var targetAccount = accountRepository.findByUuidForUpdate(reversalPayment.getTargetAccountUuid())
                    .orElseThrow(() -> new AccountNotFoundException(reversalPayment.getTargetAccountUuid()));
            targetAccount.deposit(reversalPayment.getAmount());
            accountRepository.save(targetAccount);
        }

        reversalPayment.complete();
        var savedReversalPayment = paymentRepository.save(reversalPayment);
        savedReversalPayment.revert(paymentToBeReversed);
        paymentRepository.updateReversedPayment(paymentToBeReversed);

        ledgerRecorder.recordReversal(new RecordReversalLedgerCommand(
                paymentToBeReversed.getUuid(),
                savedReversalPayment.getUuid(),
                savedReversalPayment.getReason()));

        return paymentApplicationMapper.toReversalResult(savedReversalPayment, true);
    }

    private ReversalResult getIdempotentReversal(ReversalCommand command) {
        return paymentRepository.findByRequestCode(command.requestCode())
                .map(payment -> paymentApplicationMapper.toReversalResult(PaymentRequestMatcher.match(payment, command), false))
                .orElseThrow(() -> new PaymentCodeAlreadyExists("Payment with request code " + command.requestCode() + " already exists"));

    }


}
