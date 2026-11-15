package it.bank.bankcore.account.application.usecase;

import it.bank.bankcore.account.application.command.CreateAccountCommand;
import it.bank.bankcore.account.application.mapper.AccountApplicationMapper;
import it.bank.bankcore.account.application.result.CreateAccountResult;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateAccountUseCaseTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AccountApplicationMapper accountApplicationMapper;

    @InjectMocks
    private CreateAccountUseCase useCase;

    @Test
    void execute_shouldThrowWhenUserDoesNotExist() {
        var command = new CreateAccountCommand("missing-user", "EUR");
        when(userRepository.existsByUuid("missing-user")).thenReturn(false);

        assertThrows(UserNotFoundException.class, () -> useCase.execute(command));

        verify(accountRepository, never()).save(any());
    }

    @Test
    void execute_shouldCreateAccountForExistingUser() {
        var command = new CreateAccountCommand("user-uuid", "EUR");
        var domainAccount = sampleAccount("uuid-1");
        var savedDomain = sampleAccount("uuid-2");
        var expected = new CreateAccountResult("uuid-2", "user-uuid", BigDecimal.ZERO, "EUR", AccountStatus.ACTIVE);

        when(userRepository.existsByUuid("user-uuid")).thenReturn(true);
        when(accountApplicationMapper.toDomain(command)).thenReturn(domainAccount);
        when(accountRepository.save(domainAccount)).thenReturn(savedDomain);
        when(accountApplicationMapper.toResult(savedDomain)).thenReturn(expected);

        var result = useCase.execute(command);

        assertEquals(expected, result);
        verify(accountRepository).save(domainAccount);
    }

    private Account sampleAccount(String uuid) {
        return Account.builder()
                .uuid(uuid)
                .userUuid("user-uuid")
                .balance(BigDecimal.ZERO)
                .currency("EUR")
                .status(AccountStatus.ACTIVE)
                .build();
    }
}
