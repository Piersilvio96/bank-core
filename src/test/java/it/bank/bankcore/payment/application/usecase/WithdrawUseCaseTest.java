package it.bank.bankcore.payment.application.usecase;

import it.bank.bankcore.account.domain.enums.AccountStatus;
import it.bank.bankcore.account.domain.exception.AccountNotFoundException;
import it.bank.bankcore.account.domain.model.Account;
import it.bank.bankcore.account.domain.repository.AccountRepository;
import it.bank.bankcore.ledger.application.command.RecordWithdrawLedgerCommand;
import it.bank.bankcore.ledger.application.port.LedgerRecorder;
import it.bank.bankcore.payment.application.command.WithdrawCommand;
import it.bank.bankcore.payment.application.mapper.PaymentApplicationMapper;
import it.bank.bankcore.payment.application.result.WithdrawResult;
import it.bank.bankcore.payment.application.validation.WithdrawValidationRule;
import it.bank.bankcore.payment.domain.enums.PaymentStatus;
import it.bank.bankcore.payment.domain.mapper.PaymentDomainMapper;
import it.bank.bankcore.payment.domain.model.Payment;
import it.bank.bankcore.payment.domain.repository.PaymentRepository;
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
class WithdrawUseCaseTest {

    @Mock
    private LedgerRecorder ledgerRecorder;

    @Mock
    private WithdrawValidationRule withdrawValidationRule;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private PaymentDomainMapper paymentDomainMapper;

    @Mock
    private PaymentApplicationMapper paymentApplicationMapper;

    @InjectMocks
    private WithdrawUseCase useCase;

    @Test
    void execute_shouldWithdrawAndRecordLedgerEntry() {
        var command = new WithdrawCommand("acc-uuid", new BigDecimal("25.00"), "EUR");
        var targetAccount = sampleAccount();
        var pendingPayment = samplePayment("payment-1", PaymentStatus.PENDING, "Withdraw");
        var savedPayment = samplePayment("payment-1", PaymentStatus.COMPLETED, "Withdraw");
        var expected = new WithdrawResult("payment-1", new BigDecimal("25.00"), "EUR");

        when(accountRepository.findByUuid("acc-uuid")).thenReturn(Optional.of(targetAccount));
        when(paymentDomainMapper.toDomain(command, "EUR")).thenReturn(pendingPayment);
        when(paymentRepository.save(pendingPayment)).thenReturn(savedPayment);
        when(paymentApplicationMapper.toWithdrawResult(savedPayment)).thenReturn(expected);

        var result = useCase.execute(command);

        assertEquals(expected, result);
        assertEquals(0, targetAccount.getBalance().compareTo(new BigDecimal("75.00")));

        var ledgerCaptor = ArgumentCaptor.forClass(RecordWithdrawLedgerCommand.class);
        verify(ledgerRecorder).recordWithdraw(ledgerCaptor.capture());
        assertEquals("acc-uuid", ledgerCaptor.getValue().accountId());
        assertEquals("payment-1", ledgerCaptor.getValue().paymentId());
        assertEquals(0, ledgerCaptor.getValue().amount().compareTo(new BigDecimal("25.00")));

        verify(accountRepository).save(targetAccount);
    }

    @Test
    void execute_shouldThrowWhenAccountNotFound() {
        var command = new WithdrawCommand("missing-uuid", new BigDecimal("10.00"), "EUR");
        when(accountRepository.findByUuid("missing-uuid")).thenReturn(Optional.empty());

        assertThrows(AccountNotFoundException.class, () -> useCase.execute(command));

        verify(paymentRepository, never()).save(any());
        verify(ledgerRecorder, never()).recordWithdraw(any());
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

    private Payment samplePayment(String uuid, PaymentStatus status, String reason) {
        return Payment.builder()
                .uuid(uuid)
                .targetAccountUuid("acc-uuid")
                .amount(new BigDecimal("25.00"))
                .currency("EUR")
                .reason(reason)
                .status(status)
                .build();
    }
}

