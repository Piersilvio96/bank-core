package it.bank.bankcore.account.application.usecase;

import it.bank.bankcore.account.application.mapper.AccountApplicationMapper;
import it.bank.bankcore.account.application.query.GetAccountQuery;
import it.bank.bankcore.account.application.result.GetAccountResult;
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
class GetAccountUseCaseTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private AccountApplicationMapper accountApplicationMapper;

    @InjectMocks
    private GetAccountUseCase useCase;

    @Test
    void execute_shouldReturnAccountResultWhenFound() {
        var query = new GetAccountQuery("acc-uuid");
        var account = sampleAccount();
        var expected = new GetAccountResult(
                "acc-uuid", "Mario", "Rossi", "mario.rossi@example.com", "RSSMRA80A01H501U", "+393331234567",
                "Rome", "RM", "Italy", AccountStatus.ACTIVE
        );

        when(accountRepository.findByUuid("acc-uuid")).thenReturn(Optional.of(account));
        when(accountApplicationMapper.toGetAccountResult(account)).thenReturn(expected);

        var result = useCase.execute(query);

        assertEquals(expected, result);
    }

    @Test
    void execute_shouldThrowWhenAccountMissing() {
        var query = new GetAccountQuery("missing-uuid");
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
                .balance(BigDecimal.ZERO)
                .currency("EUR")
                .status(AccountStatus.ACTIVE)
                .build();
    }
}

