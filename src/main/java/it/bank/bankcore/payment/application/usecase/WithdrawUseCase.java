package it.bank.bankcore.payment.application.usecase;

import it.bank.bankcore.account.domain.repository.AccountRepository;
import it.bank.bankcore.ledger.application.command.RecordWithdrawLedgerCommand;
import it.bank.bankcore.ledger.application.port.LedgerRecorder;
import it.bank.bankcore.payment.application.command.WithdrawCommand;
import it.bank.bankcore.payment.application.mapper.PaymentApplicationMapper;
import it.bank.bankcore.payment.application.result.WithdrawResult;
import it.bank.bankcore.payment.application.validation.WithdrawValidationRule;
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
public class WithdrawUseCase implements UseCase<WithdrawCommand, WithdrawResult> {

    private static final String WITHDRAW_REASON = "Withdraw";

    private final LedgerRecorder ledgerRecorder;
    private final WithdrawValidationRule withdrawValidationRule;
    private final AccountRepository accountRepository;
    private final PaymentRepository paymentRepository;
    private final PaymentDomainMapper paymentDomainMapper;
    private final PaymentApplicationMapper paymentApplicationMapper;

    @Override
    public WithdrawResult execute(WithdrawCommand command) {
        return paymentRepository.findByRequestCode(command.requestCode())
                .map(payment -> paymentApplicationMapper.toWithdrawResult(payment, false))
                .orElseGet(() -> processNewWithdraw(command));
    }

    private WithdrawResult processNewWithdraw(WithdrawCommand command) {
        var targetAccount = withdrawValidationRule.loadAndValidate(command);

        var payment = paymentDomainMapper.toDomain(command);
        payment.complete();
        final Payment savedPayment;
        try {
            savedPayment = paymentRepository.save(payment);
        } catch (PaymentCodeAlreadyExists exception) {
            return getIdempotentWithdraw(command.requestCode());
        }

        targetAccount.withdraw(command.amount());
        accountRepository.save(targetAccount);

        ledgerRecorder.recordWithdraw(new RecordWithdrawLedgerCommand(
                targetAccount.getUuid(),
                savedPayment.getUuid(),
                command.amount(),
                targetAccount.getCurrency(),
                WITHDRAW_REASON
        ));

        return paymentApplicationMapper.toWithdrawResult(savedPayment, true);
    }

    private WithdrawResult getIdempotentWithdraw(String requestCode) {
        return paymentRepository.findByRequestCode(requestCode)
                .map(payment -> paymentApplicationMapper.toWithdrawResult(payment, false))
                .orElseThrow(() -> new PaymentCodeAlreadyExists("Payment with request code " + requestCode + " already exists"));
    }
}
