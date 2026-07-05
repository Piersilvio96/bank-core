package it.bank.bankcore.account.application.usecase;

import it.bank.bankcore.account.application.command.CreateAccountCommand;
import it.bank.bankcore.account.application.mapper.AccountApplicationMapper;
import it.bank.bankcore.account.application.result.CreateAccountResult;
import it.bank.bankcore.account.domain.enums.AccountStatus;
import it.bank.bankcore.account.domain.exception.AccountAlreadyExistsException;
import it.bank.bankcore.account.domain.model.Account;
import it.bank.bankcore.account.domain.repository.AccountRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateAccountUseCaseTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private AccountApplicationMapper accountApplicationMapper;

    @InjectMocks
    private CreateAccountUseCase useCase;

    @Test
    void execute_shouldThrowWhenAccountAlreadyExists() {
        var command = sampleCommand();
        when(accountRepository.existsByFiscalCodeOrEmail(command.fiscalCode(), command.email())).thenReturn(true);

        assertThrows(AccountAlreadyExistsException.class, () -> useCase.execute(command));

        verify(accountRepository, never()).save(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void execute_shouldCreateAccountWhenNotExists() {
        var command = sampleCommand();
        var domainAccount = sampleAccount("uuid-1", BigDecimal.ZERO);
        var savedDomain = sampleAccount("uuid-2", BigDecimal.ZERO);
        var expected = new CreateAccountResult(
                "uuid-2", "Mario", "Rossi", "mario.rossi@example.com", "RSSMRA80A01H501U", "+393331234567",
                "Rome", "RM", "Italy", BigDecimal.ZERO, "EUR"
        );

        when(accountRepository.existsByFiscalCodeOrEmail(command.fiscalCode(), command.email())).thenReturn(false);
        when(accountApplicationMapper.toDomain(command)).thenReturn(domainAccount);
        when(accountRepository.save(domainAccount)).thenReturn(savedDomain);
        when(accountApplicationMapper.toResult(savedDomain)).thenReturn(expected);

        var result = useCase.execute(command);

        assertEquals(expected, result);
        verify(accountRepository).save(domainAccount);
    }

    private CreateAccountCommand sampleCommand() {
        return new CreateAccountCommand(
                "Mario", "Rossi", "mario.rossi@example.com", "+393331234567", "RSSMRA80A01H501U",
                "Rome", "RM", "Italy", "EUR"
        );
    }

    private Account sampleAccount(String uuid, BigDecimal balance) {
        return Account.builder()
                .uuid(uuid)
                .firstName("Mario")
                .lastName("Rossi")
                .email("mario.rossi@example.com")
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
}

