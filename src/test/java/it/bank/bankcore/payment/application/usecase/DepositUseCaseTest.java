package it.bank.bankcore.payment.application.usecase;

import it.bank.bankcore.account.domain.enums.AccountStatus;
import it.bank.bankcore.account.domain.exception.AccountNotFoundException;
import it.bank.bankcore.account.domain.model.Account;
import it.bank.bankcore.account.domain.repository.AccountRepository;
import it.bank.bankcore.ledger.application.command.RecordDepositLedgerCommand;
import it.bank.bankcore.ledger.application.port.LedgerRecorder;
import it.bank.bankcore.payment.application.command.DepositCommand;
import it.bank.bankcore.payment.application.mapper.PaymentApplicationMapper;
import it.bank.bankcore.payment.application.result.DepositResult;
import it.bank.bankcore.payment.application.validation.DepositValidationRule;
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
class DepositUseCaseTest {

    @Mock
    private LedgerRecorder ledgerRecorder;

    @Mock
    private DepositValidationRule depositValidationRule;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private PaymentDomainMapper paymentDomainMapper;

    @Mock
    private PaymentApplicationMapper paymentApplicationMapper;

    @InjectMocks
    private DepositUseCase useCase;

    @Test
    void execute_shouldDepositAndRecordLedgerEntry() {
        var command = new DepositCommand("acc-uuid", new BigDecimal("25.00"), "EUR", "req-deposit-1");
        var targetAccount = sampleAccount();
        var pendingPayment = samplePayment("payment-1", PaymentStatus.PENDING);
        var savedPayment = samplePayment("payment-1", PaymentStatus.COMPLETED);
        var expected = new DepositResult("payment-1", new BigDecimal("25.00"), "EUR", true);

        when(depositValidationRule.loadAndValidate(command)).thenReturn(targetAccount);
        when(paymentDomainMapper.toDomain(command)).thenReturn(pendingPayment);
        when(paymentRepository.save(pendingPayment)).thenReturn(savedPayment);
        when(paymentApplicationMapper.toDepositResult(savedPayment, true)).thenReturn(expected);

        var result = useCase.execute(command);

        assertEquals(expected, result);
        assertEquals(0, targetAccount.getBalance().compareTo(new BigDecimal("125.00")));

        var ledgerCaptor = ArgumentCaptor.forClass(RecordDepositLedgerCommand.class);
        verify(ledgerRecorder).recordDeposit(ledgerCaptor.capture());
        assertEquals("acc-uuid", ledgerCaptor.getValue().accountId());
        assertEquals("payment-1", ledgerCaptor.getValue().paymentId());
        assertEquals(0, ledgerCaptor.getValue().amount().compareTo(new BigDecimal("25.00")));
        assertEquals("EUR", ledgerCaptor.getValue().currency());

        verify(accountRepository).save(targetAccount);
    }

    @Test
    void execute_shouldReturnExistingPaymentWhenRequestCodeAlreadyProcessed() {
        var command = new DepositCommand("acc-uuid", new BigDecimal("25.00"), "EUR", "req-deposit-existing");
        var existingPayment = samplePayment("payment-existing", PaymentStatus.COMPLETED);
        var expected = new DepositResult("payment-existing", new BigDecimal("25.00"), "EUR", false);

        when(paymentRepository.findByRequestCode(command.requestCode())).thenReturn(Optional.of(existingPayment));
        when(paymentApplicationMapper.toDepositResult(existingPayment, false)).thenReturn(expected);

        var result = useCase.execute(command);

        assertEquals(expected, result);
        verify(paymentRepository, never()).save(any());
        verify(ledgerRecorder, never()).recordDeposit(any());
        verify(accountRepository, never()).save(any());
    }

    @Test
    void execute_shouldReturnExistingPaymentWhenConcurrentSaveDetectsDuplicateRequestCode() {
        var command = new DepositCommand("acc-uuid", new BigDecimal("25.00"), "EUR", "req-deposit-race");
        var targetAccount = sampleAccount();
        var pendingPayment = samplePayment("payment-race", PaymentStatus.PENDING);
        var existingPayment = samplePayment("payment-existing", PaymentStatus.COMPLETED);
        var expected = new DepositResult("payment-existing", new BigDecimal("25.00"), "EUR", false);

        when(paymentRepository.findByRequestCode(command.requestCode()))
                .thenReturn(Optional.empty())
                .thenReturn(Optional.of(existingPayment));
        when(depositValidationRule.loadAndValidate(command)).thenReturn(targetAccount);
        when(paymentDomainMapper.toDomain(command)).thenReturn(pendingPayment);
        when(paymentRepository.save(pendingPayment))
                .thenThrow(new PaymentCodeAlreadyExists("Payment with request code " + command.requestCode() + " already exists"));
        when(paymentApplicationMapper.toDepositResult(existingPayment, false)).thenReturn(expected);

        var result = useCase.execute(command);

        assertEquals(expected, result);
        verify(ledgerRecorder, never()).recordDeposit(any());
        verify(accountRepository, never()).save(any());
    }

    @Test
    void execute_shouldThrowWhenAccountNotFound() {
        var command = new DepositCommand("missing-uuid", new BigDecimal("10.00"), "EUR", "req-deposit-2");
        when(depositValidationRule.loadAndValidate(command)).thenThrow(new AccountNotFoundException("missing-uuid"));
        when(paymentRepository.findByRequestCode(command.requestCode())).thenReturn(Optional.empty());

        assertThrows(AccountNotFoundException.class, () -> useCase.execute(command));

        verify(paymentRepository, never()).save(any());
        verify(ledgerRecorder, never()).recordDeposit(any());
    }

    private Account sampleAccount() {
        return Account.builder()
                .uuid("acc-uuid")
                .firstName("Mario")
                .lastName("Rossi")
                .email("mario.rossi@example.com")
                .fiscalCode("RSSMRA80A01H501U")
                .phoneNumber("+393331234567")
                .city("Rome")
                .state("RM")
                .country("Italy")
                .balance(new BigDecimal("100.00"))
                .currency("EUR")
                .status(AccountStatus.ACTIVE)
                .build();
    }

    private Payment samplePayment(String uuid, PaymentStatus status) {
        return Payment.builder()
                .uuid(uuid)
                .targetAccountUuid("acc-uuid")
                .amount(new BigDecimal("25.00"))
                .currency("EUR")
                .reason("Deposit")
                .status(status)
                .build();
    }
}
