package it.bank.bankcore.account.application.usecase;

import it.bank.bankcore.account.application.mapper.AccountApplicationMapper;
import it.bank.bankcore.account.application.query.ListAccountsQuery;
import it.bank.bankcore.account.application.result.GetAccountResult;
import it.bank.bankcore.account.domain.enums.AccountStatus;
import it.bank.bankcore.account.domain.model.Account;
import it.bank.bankcore.account.domain.repository.AccountRepository;
import it.bank.bankcore.user.domain.exception.UserNotFoundException;
import it.bank.bankcore.user.domain.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ListAccountsUseCaseTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AccountApplicationMapper accountApplicationMapper;

    @InjectMocks
    private ListAccountsUseCase useCase;

    @Test
    void execute_shouldReturnAllAccountsOfUser() {
        var account = Account.builder()
                .uuid("acc-uuid")
                .userUuid("user-uuid")
                .balance(BigDecimal.TEN)
                .currency("EUR")
                .status(AccountStatus.ACTIVE)
                .build();
        var expected = new GetAccountResult("acc-uuid", "user-uuid", BigDecimal.TEN, "EUR", AccountStatus.ACTIVE);

        when(userRepository.existsByUuid("user-uuid")).thenReturn(true);
        when(accountRepository.findAllByUserUuid("user-uuid")).thenReturn(List.of(account));
        when(accountApplicationMapper.toGetAccountResult(account)).thenReturn(expected);

        var result = useCase.execute(new ListAccountsQuery("user-uuid"));

        assertEquals(List.of(expected), result);
    }

    @Test
    void execute_shouldThrowWhenUserMissing() {
        when(userRepository.existsByUuid("missing-user")).thenReturn(false);

        assertThrows(UserNotFoundException.class, () -> useCase.execute(new ListAccountsQuery("missing-user")));
    }
}
