package it.bank.bankcore.payment.application.usecase;

import it.bank.bankcore.account.domain.repository.AccountRepository;
import it.bank.bankcore.ledger.application.command.RecordDepositLedgerCommand;
import it.bank.bankcore.ledger.application.port.LedgerRecorder;
import it.bank.bankcore.payment.application.command.DepositCommand;
import it.bank.bankcore.payment.application.mapper.PaymentApplicationMapper;
import it.bank.bankcore.payment.application.result.DepositResult;
import it.bank.bankcore.payment.application.validation.DepositValidationRule;
import it.bank.bankcore.payment.domain.mapper.PaymentDomainMapper;
import it.bank.bankcore.payment.domain.model.Payment;
import it.bank.bankcore.payment.domain.repository.PaymentRepository;
import it.bank.bankcore.payment.infrastructure.exception.PaymentCodeAlreadyExists;
import it.bank.bankcore.shared.application.UseCase;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class DepositUseCase implements UseCase<DepositCommand, DepositResult> {

    private static final String DEPOSIT_REASON = "Deposit";

    private final LedgerRecorder ledgerRecorder;
    private final DepositValidationRule depositValidationRule;
    private final AccountRepository accountRepository;
    private final PaymentRepository paymentRepository;
    private final PaymentDomainMapper paymentDomainMapper;
    private final PaymentApplicationMapper paymentApplicationMapper;

    @Override
    public DepositResult execute(DepositCommand command) {
        return paymentRepository.findByRequestCode(command.requestCode())
                .map(payment -> paymentApplicationMapper.toDepositResult(payment, false))
                .orElseGet(() -> processNewDeposit(command));
    }

    private DepositResult processNewDeposit(DepositCommand command) {
        var targetAccount = depositValidationRule.loadAndValidate(command);

        var payment = paymentDomainMapper.toDomain(command);
        payment.complete();
        final Payment savedPayment;
        try {
            savedPayment = paymentRepository.save(payment);
        } catch (PaymentCodeAlreadyExists exception) {
            return getIdempotentDeposit(command.requestCode());
        }

        targetAccount.deposit(command.amount());
        accountRepository.save(targetAccount);

        ledgerRecorder.recordDeposit(new RecordDepositLedgerCommand(
                targetAccount.getUuid(),
                savedPayment.getUuid(),
                command.amount(),
                targetAccount.getCurrency(),
                DEPOSIT_REASON
        ));

        return paymentApplicationMapper.toDepositResult(savedPayment, true);
    }

    private DepositResult getIdempotentDeposit(String requestCode) {
        return paymentRepository.findByRequestCode(requestCode)
                .map(payment -> paymentApplicationMapper.toDepositResult(payment, false))
                .orElseThrow(() -> new PaymentCodeAlreadyExists("Payment with request code " + requestCode + " already exists"));
    }
}
