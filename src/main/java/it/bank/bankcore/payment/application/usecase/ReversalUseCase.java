package it.bank.bankcore.payment.application.usecase;

import it.bank.bankcore.account.domain.exception.AccountNotFoundException;
import it.bank.bankcore.account.domain.repository.AccountRepository;
import it.bank.bankcore.ledger.application.command.RecordReversalLedgerCommand;
import it.bank.bankcore.ledger.application.port.LedgerRecorder;
import it.bank.bankcore.payment.application.command.ReversalCommand;
import it.bank.bankcore.payment.application.mapper.PaymentApplicationMapper;
import it.bank.bankcore.payment.application.result.ReversalResult;
import it.bank.bankcore.payment.domain.exception.PaymentNotFoundException;
import it.bank.bankcore.payment.domain.mapper.PaymentDomainMapper;
import it.bank.bankcore.payment.domain.model.Payment;
import it.bank.bankcore.payment.domain.repository.PaymentRepository;
import it.bank.bankcore.payment.infrastructure.exception.PaymentCodeAlreadyExists;
import it.bank.bankcore.shared.application.UseCase;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

@Service
@RequiredArgsConstructor
@Transactional
public class ReversalUseCase implements UseCase<ReversalCommand, ReversalResult> {

    private final AccountRepository accountRepository;
    private final PaymentRepository paymentRepository;
    private final PaymentDomainMapper paymentDomainMapper;
    private final PaymentApplicationMapper paymentApplicationMapper;
    private final LedgerRecorder ledgerRecorder;

    @Override
    public ReversalResult execute(ReversalCommand input) {
        return paymentRepository.findByRequestCode(input.requestCode())
                .map(payment -> paymentApplicationMapper.toReversalResult(payment, false))
                .orElseGet(() -> processNewReversalCommand(input));
    }

    private ReversalResult processNewReversalCommand(ReversalCommand input) {
        var paymentToBeReversed = paymentRepository.findByPaymentId(input.paymentId())
                .orElseThrow(() -> new PaymentNotFoundException(input.paymentId()));

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
        final Payment savedReversalPayment;
        try {
            savedReversalPayment = paymentRepository.save(reversalPayment);
        } catch (PaymentCodeAlreadyExists exception) {
            return getIdempotentReversal(input.requestCode());
        }
        savedReversalPayment.revert(paymentToBeReversed);
        paymentRepository.updateReversedPayment(paymentToBeReversed);

        ledgerRecorder.recordReversal(new RecordReversalLedgerCommand(
                paymentToBeReversed.getUuid(),
                savedReversalPayment.getUuid(),
                savedReversalPayment.getReason()));

        return paymentApplicationMapper.toReversalResult(savedReversalPayment, true);
    }

    private ReversalResult getIdempotentReversal(String requestCode) {
        return paymentRepository.findByRequestCode(requestCode)
                .map(payment -> paymentApplicationMapper.toReversalResult(payment, false))
                .orElseThrow(() -> new PaymentCodeAlreadyExists("Payment with request code " + requestCode + " already exists"));

    }


}
