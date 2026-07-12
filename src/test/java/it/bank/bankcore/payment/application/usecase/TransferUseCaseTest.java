package it.bank.bankcore.payment.application.usecase;

import it.bank.bankcore.account.domain.enums.AccountStatus;
import it.bank.bankcore.account.domain.exception.AccountNotFoundException;
import it.bank.bankcore.account.domain.model.Account;
import it.bank.bankcore.account.domain.repository.AccountRepository;
import it.bank.bankcore.ledger.application.command.RecordTransferLedgerCommand;
import it.bank.bankcore.ledger.application.port.LedgerRecorder;
import it.bank.bankcore.payment.application.command.TransferCommand;
import it.bank.bankcore.payment.application.mapper.PaymentApplicationMapper;
import it.bank.bankcore.payment.application.result.TransferResult;
import it.bank.bankcore.payment.application.validation.TransferValidationRule;
import it.bank.bankcore.payment.application.validation.TransferValidationResult;
import it.bank.bankcore.payment.domain.enums.PaymentStatus;
import it.bank.bankcore.payment.domain.mapper.PaymentDomainMapper;
import it.bank.bankcore.payment.domain.model.Payment;
import it.bank.bankcore.payment.domain.repository.PaymentRepository;
import it.bank.bankcore.payment.infrastructure.exception.PaymentCodeAlreadyExists;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransferUseCaseTest {

    @Mock
    private LedgerRecorder ledgerRecorder;

    @Mock
    private TransferValidationRule transferValidationRule;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private PaymentDomainMapper paymentDomainMapper;

    @Mock
    private PaymentApplicationMapper paymentApplicationMapper;

    @InjectMocks
    private TransferUseCase useCase;

    @Test
    void execute_shouldTransferAndRecordLedgerEntries() {
        var command = new TransferCommand("source-uuid", "target-uuid", new BigDecimal("30.00"), "EUR", "rent", "req-transfer-1");
        var sourceAccount = sampleAccount("source-uuid", new BigDecimal("100.00"));
        var targetAccount = sampleAccount("target-uuid", new BigDecimal("20.00"));
        var pendingPayment = samplePayment("payment-1", PaymentStatus.PENDING, "rent");
        var savedPayment = samplePayment("payment-1", PaymentStatus.COMPLETED, "rent");
        var expected = new TransferResult("payment-1", new BigDecimal("30.00"), "EUR", true);

        when(transferValidationRule.loadAndValidate(command)).thenReturn(new TransferValidationResult(sourceAccount, targetAccount));
        when(paymentDomainMapper.toDomain(command)).thenReturn(pendingPayment);
        when(paymentRepository.save(pendingPayment)).thenReturn(savedPayment);
        when(paymentApplicationMapper.toTransferResult(savedPayment, true)).thenReturn(expected);

        var result = useCase.execute(command);

        assertEquals(expected, result);
        assertEquals(0, sourceAccount.getBalance().compareTo(new BigDecimal("70.00")));
        assertEquals(0, targetAccount.getBalance().compareTo(new BigDecimal("50.00")));

        var ledgerCaptor = ArgumentCaptor.forClass(RecordTransferLedgerCommand.class);
        verify(ledgerRecorder).recordTransfer(ledgerCaptor.capture());
        assertEquals("source-uuid", ledgerCaptor.getValue().sourceAccountId());
        assertEquals("target-uuid", ledgerCaptor.getValue().targetAccountId());
        assertEquals("payment-1", ledgerCaptor.getValue().paymentId());

        verify(accountRepository).save(sourceAccount);
        verify(accountRepository).save(targetAccount);
    }

    @Test
    void execute_shouldReturnExistingPaymentWhenRequestCodeAlreadyProcessed() {
        var command = new TransferCommand("source-uuid", "target-uuid", new BigDecimal("30.00"), "EUR", "rent", "req-transfer-existing");
        var existingPayment = samplePayment("payment-existing", PaymentStatus.COMPLETED, "rent");
        var expected = new TransferResult("payment-existing", new BigDecimal("30.00"), "EUR", false);

        when(paymentRepository.findByRequestCode(command.requestCode())).thenReturn(Optional.of(existingPayment));
        when(paymentApplicationMapper.toTransferResult(existingPayment, false)).thenReturn(expected);

        var result = useCase.execute(command);

        assertEquals(expected, result);
        verify(paymentRepository, never()).save(any());
        verify(ledgerRecorder, never()).recordTransfer(any());
        verify(accountRepository, never()).save(any());
    }

    @Test
    void execute_shouldReturnExistingPaymentWhenConcurrentSaveDetectsDuplicateRequestCode() {
        var command = new TransferCommand("source-uuid", "target-uuid", new BigDecimal("30.00"), "EUR", "rent", "req-transfer-race");
        var sourceAccount = sampleAccount("source-uuid", new BigDecimal("100.00"));
        var targetAccount = sampleAccount("target-uuid", new BigDecimal("20.00"));
        var pendingPayment = samplePayment("payment-race", PaymentStatus.PENDING, "rent");
        var existingPayment = samplePayment("payment-existing", PaymentStatus.COMPLETED, "rent");
        var expected = new TransferResult("payment-existing", new BigDecimal("30.00"), "EUR", false);

        when(paymentRepository.findByRequestCode(command.requestCode()))
                .thenReturn(Optional.empty())
                .thenReturn(Optional.of(existingPayment));
        when(transferValidationRule.loadAndValidate(command)).thenReturn(new TransferValidationResult(sourceAccount, targetAccount));
        when(paymentDomainMapper.toDomain(command)).thenReturn(pendingPayment);
        when(paymentRepository.save(pendingPayment))
                .thenThrow(new PaymentCodeAlreadyExists("Payment with request code " + command.requestCode() + " already exists"));
        when(paymentApplicationMapper.toTransferResult(existingPayment, false)).thenReturn(expected);

        var result = useCase.execute(command);

        assertEquals(expected, result);
        verify(ledgerRecorder, never()).recordTransfer(any());
    }

    @Test
    void execute_shouldThrowWhenSourceAccountMissing() {
        var command = new TransferCommand("source-uuid", "target-uuid", new BigDecimal("10.00"), "EUR", "rent", "req-transfer-2");
        when(paymentRepository.findByRequestCode(command.requestCode())).thenReturn(Optional.empty());
        when(transferValidationRule.loadAndValidate(command)).thenThrow(new AccountNotFoundException("source-uuid"));

        assertThrows(AccountNotFoundException.class, () -> useCase.execute(command));

        verify(paymentRepository, never()).save(any());
        verify(ledgerRecorder, never()).recordTransfer(any());
    }

    @Test
    void execute_shouldThrowWhenTargetAccountMissing() {
        var command = new TransferCommand("source-uuid", "target-uuid", new BigDecimal("10.00"), "EUR", "rent", "req-transfer-3");
        when(paymentRepository.findByRequestCode(command.requestCode())).thenReturn(Optional.empty());
        when(transferValidationRule.loadAndValidate(command)).thenThrow(new AccountNotFoundException("target-uuid"));

        assertThrows(AccountNotFoundException.class, () -> useCase.execute(command));

        verify(paymentRepository, never()).save(any());
        verify(ledgerRecorder, never()).recordTransfer(any());
    }

    private Account sampleAccount(String uuid, BigDecimal balance) {
        return Account.builder()
                .uuid(uuid)
                .firstName("Mario")
                .lastName("Rossi")
                .email(uuid + "@example.com")
                .fiscalCode("RSSMRA80A01H501U")
                .phoneNumber("+393331234567")
                .city("Rome")
                .state("RM")
                .country("Italy")
                .balance(balance)
                .currency("EUR")
                .status(AccountStatus.ACTIVE)
                .build();
    }

    private Payment samplePayment(String uuid, PaymentStatus status, String reason) {
        return Payment.builder()
                .uuid(uuid)
                .sourceAccountUuid("source-uuid")
                .targetAccountUuid("target-uuid")
                .amount(new BigDecimal("30.00"))
                .currency("EUR")
                .reason(reason)
                .status(status)
                .build();
    }
}
