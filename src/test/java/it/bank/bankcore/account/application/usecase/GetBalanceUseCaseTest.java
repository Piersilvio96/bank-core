package it.bank.bankcore.account.application.usecase;

import it.bank.bankcore.account.application.mapper.AccountApplicationMapper;
import it.bank.bankcore.account.application.query.GetBalanceQuery;
import it.bank.bankcore.account.application.result.GetAccountBalanceResult;
import it.bank.bankcore.account.domain.enums.AccountStatus;
import it.bank.bankcore.account.domain.exception.AccountNotFoundException;
import it.bank.bankcore.account.domain.model.Account;
import it.bank.bankcore.account.domain.repository.AccountRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetBalanceUseCaseTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private AccountApplicationMapper accountApplicationMapper;

    @InjectMocks
    private GetBalanceUseCase useCase;

    @Test
    void execute_shouldReturnBalanceWhenAccountFound() {
        var query = new GetBalanceQuery("acc-uuid");
        var account = sampleAccount();
        var expected = new GetAccountBalanceResult(new BigDecimal("120.50"), "EUR");

        when(accountRepository.findByUuid("acc-uuid")).thenReturn(Optional.of(account));
        when(accountApplicationMapper.toGetAccountBalanceResult(account)).thenReturn(expected);

        var result = useCase.execute(query);

        assertEquals(expected, result);
    }

    @Test
    void execute_shouldThrowWhenAccountMissing() {
        var query = new GetBalanceQuery("missing-uuid");
        when(accountRepository.findByUuid("missing-uuid")).thenReturn(Optional.empty());

        assertThrows(AccountNotFoundException.class, () -> useCase.execute(query));
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
                .balance(new BigDecimal("120.50"))
                .currency("EUR")
                .status(AccountStatus.ACTIVE)
                .build();
    }
}

