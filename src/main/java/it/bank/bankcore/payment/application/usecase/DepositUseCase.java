package it.bank.bankcore.payment.application.usecase;

import it.bank.bankcore.account.domain.repository.AccountRepository;
import it.bank.bankcore.ledger.application.command.RecordTransferLedgerCommand;
import it.bank.bankcore.ledger.application.port.LedgerRecorder;
import it.bank.bankcore.ledger.domain.enums.LedgerType;
import it.bank.bankcore.payment.api.request.DepositRequest;
import it.bank.bankcore.payment.api.response.DepositResponse;
import it.bank.bankcore.payment.application.validation.DepositValidationRule;
import it.bank.bankcore.payment.domain.enums.PaymentStatus;
import it.bank.bankcore.payment.domain.model.PaymentEntity;
import it.bank.bankcore.payment.domain.repository.PaymentRepository;
import it.bank.bankcore.shared.application.UseCase;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class DepositUseCase implements UseCase<DepositRequest, DepositResponse> {

    private final LedgerRecorder ledgerRecorder;
    private final DepositValidationRule depositValidationRule;
    private final AccountRepository accountRepository;
    private final PaymentRepository paymentRepository;
    private final String DEPOSIT_REASON = "Deposit";

    @Override
    public DepositResponse execute(DepositRequest input) {
        // Implement the logic to perform a deposit operation
        depositValidationRule.validate(input);
        // For example, you can call a repository to update the account balance
        var targetAccount = accountRepository.findByUuid(input.accountUuid())
                .orElseThrow(() -> new IllegalArgumentException("Target account not found."));

        var payment = PaymentEntity.builder()
                .sourceAccount(null)
                .targetAccount(targetAccount)
                .amount(input.amount())
                .currency(targetAccount.getCurrency())
                .reason(DEPOSIT_REASON)
                .status(PaymentStatus.PENDING)
                .build();

        // Update the account balance
        targetAccount.setBalance(targetAccount.getBalance().add(input.amount()));
        accountRepository.save(targetAccount);
        paymentRepository.save(payment);

        // Record the ledger entry for the deposit
        ledgerRecorder.recordTransfer(new RecordTransferLedgerCommand(
                targetAccount.getUuid(),
                payment.getUuid(),
                LedgerType.CREDIT,
                input.amount(),
                targetAccount.getCurrency(),
                DEPOSIT_REASON
        ));

        payment.setStatus(PaymentStatus.COMPLETED);
        paymentRepository.save(payment);

        // and return a DepositResponse object.
        return DepositResponse.builder()
                .paymentId(payment.getUuid())
                .amount(input.amount())
                .currency(targetAccount.getCurrency())
                .build();
    }
}
