package it.bank.bankcore.payment.application.usecase;

import it.bank.bankcore.account.domain.repository.AccountRepository;
import it.bank.bankcore.ledger.application.command.RecordTransferLedgerCommand;
import it.bank.bankcore.ledger.application.port.LedgerRecorder;
import it.bank.bankcore.ledger.domain.enums.LedgerType;
import it.bank.bankcore.payment.api.mapper.DepositMapper;
import it.bank.bankcore.payment.api.response.DepositResponse;
import it.bank.bankcore.payment.application.command.DepositCommand;
import it.bank.bankcore.payment.application.mapper.PaymentApplicationMapper;
import it.bank.bankcore.payment.application.validation.DepositValidationRule;
import it.bank.bankcore.payment.domain.enums.PaymentStatus;
import it.bank.bankcore.payment.domain.mapper.PaymentDomainMapper;
import it.bank.bankcore.payment.domain.repository.PaymentRepository;
import it.bank.bankcore.shared.application.UseCase;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class DepositUseCase implements UseCase<DepositCommand, DepositResponse> {

    private static final String DEPOSIT_REASON = "Deposit";

    private final LedgerRecorder ledgerRecorder;
    private final DepositValidationRule depositValidationRule;
    private final AccountRepository accountRepository;
    private final PaymentRepository paymentRepository;
    private final PaymentDomainMapper paymentDomainMapper;
    private final PaymentApplicationMapper paymentApplicationMapper;
    private final DepositMapper depositMapper;

    @Override
    public DepositResponse execute(DepositCommand command) {
        depositValidationRule.validate(command);

        var targetAccount = accountRepository.findByUuid(command.accountUuid())
                .orElseThrow(() -> new IllegalArgumentException("Target account not found."));

        var payment = paymentDomainMapper.toDomain(command, targetAccount.getCurrency());
        payment.setStatus(PaymentStatus.COMPLETED);
        var savedPayment = paymentRepository.save(payment);

        targetAccount.deposit(command.amount());
        accountRepository.save(targetAccount);

        ledgerRecorder.recordTransfer(new RecordTransferLedgerCommand(
                targetAccount.getUuid(),
                savedPayment.getUuid(),
                LedgerType.CREDIT,
                command.amount(),
                targetAccount.getCurrency(),
                DEPOSIT_REASON
        ));

        var result = paymentApplicationMapper.toDepositResult(savedPayment);

        return depositMapper.toResponse(result);
    }
}
