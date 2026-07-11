package it.bank.bankcore.payment.application.usecase;

import it.bank.bankcore.account.domain.repository.AccountRepository;
import it.bank.bankcore.ledger.application.command.RecordTransferLedgerCommand;
import it.bank.bankcore.ledger.application.port.LedgerRecorder;
import it.bank.bankcore.payment.application.command.TransferCommand;
import it.bank.bankcore.payment.application.mapper.PaymentApplicationMapper;
import it.bank.bankcore.payment.application.result.TransferResult;
import it.bank.bankcore.payment.application.validation.TransferValidationRule;
import it.bank.bankcore.payment.domain.mapper.PaymentDomainMapper;
import it.bank.bankcore.payment.domain.repository.PaymentRepository;
import it.bank.bankcore.shared.application.UseCase;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class TransferUseCase implements UseCase<TransferCommand, TransferResult> {

    private final LedgerRecorder ledgerRecorder;
    private final TransferValidationRule transferValidationRule;
    private final AccountRepository accountRepository;
    private final PaymentRepository paymentRepository;
    private final PaymentDomainMapper paymentDomainMapper;
    private final PaymentApplicationMapper paymentApplicationMapper;

    @Override
    public TransferResult execute(TransferCommand command) {

        return paymentRepository.findByRequestCode(command.requestCode())
                .map(paymentApplicationMapper::toTransferResult)
                .orElseGet(() -> processNewTransfer(command));
    }

    private TransferResult processNewTransfer(TransferCommand command) {
        var validationResult = transferValidationRule.loadAndValidate(command);
        var sourceAccount = validationResult.sourceAccount();
        var targetAccount = validationResult.targetAccount();

        var payment = paymentDomainMapper.toDomain(command);
        payment.complete();
        var savedPayment = paymentRepository.save(payment);

        sourceAccount.withdraw(command.amount());
        accountRepository.save(sourceAccount);

        targetAccount.deposit(command.amount());
        accountRepository.save(targetAccount);

        ledgerRecorder.recordTransfer(new RecordTransferLedgerCommand(
                sourceAccount.getUuid(),
                targetAccount.getUuid(),
                savedPayment.getUuid(),
                command.amount(),
                targetAccount.getCurrency(),
                savedPayment.getReason()
        ));

        return paymentApplicationMapper.toTransferResult(savedPayment);

    }
}
