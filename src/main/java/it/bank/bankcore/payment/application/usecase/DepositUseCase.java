package it.bank.bankcore.payment.application.usecase;

import it.bank.bankcore.account.domain.exception.AccountNotFoundException;
import it.bank.bankcore.account.domain.repository.AccountRepository;
import it.bank.bankcore.ledger.application.command.RecordDepositLedgerCommand;
import it.bank.bankcore.ledger.application.port.LedgerRecorder;
import it.bank.bankcore.payment.application.command.DepositCommand;
import it.bank.bankcore.payment.application.mapper.PaymentApplicationMapper;
import it.bank.bankcore.payment.application.result.DepositResult;
import it.bank.bankcore.payment.application.validation.DepositValidationRule;
import it.bank.bankcore.payment.domain.mapper.PaymentDomainMapper;
import it.bank.bankcore.payment.domain.repository.PaymentRepository;
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
        depositValidationRule.validate(command);

        var targetAccount = accountRepository.findByUuidForUpdate(command.accountUuid())
                .orElseThrow(() -> new AccountNotFoundException(command.accountUuid()));

        var payment = paymentDomainMapper.toDomain(command);
        payment.complete();
        var savedPayment = paymentRepository.save(payment);

        targetAccount.deposit(command.amount());
        accountRepository.save(targetAccount);

        ledgerRecorder.recordDeposit(new RecordDepositLedgerCommand(
                targetAccount.getUuid(),
                savedPayment.getUuid(),
                command.amount(),
                targetAccount.getCurrency(),
                DEPOSIT_REASON
        ));

        return paymentApplicationMapper.toDepositResult(savedPayment);
    }
}
