package it.bank.bankcore.account.application.mapper;

import it.bank.bankcore.account.application.command.CreateAccountCommand;
import it.bank.bankcore.account.domain.enums.AccountStatus;
import it.bank.bankcore.account.domain.mapper.AccountDomainMapper;
import it.bank.bankcore.account.domain.model.Account;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountApplicationMapperTest {

    @Mock
    private AccountDomainMapper accountDomainMapper;

    @InjectMocks
    private AccountApplicationMapper mapper;

    @Test
    void toDomain_shouldDelegateToDomainMapper() {
        var command = new CreateAccountCommand(
                "Mario", "Rossi", "mario.rossi@example.com", "+393331234567", "RSSMRA80A01H501U",
                "Rome", "RM", "Italy", "EUR"
        );
        var expectedDomain = sampleAccount();

        when(accountDomainMapper.toDomain(command)).thenReturn(expectedDomain);

        var result = mapper.toDomain(command);

        assertEquals(expectedDomain, result);
    }

    @Test
    void toResult_shouldMapCreateAccountResult() {
        var account = sampleAccount();

        var result = mapper.toResult(account);

        assertEquals(account.getUuid(), result.uuid());
        assertEquals(account.getFiscalCode(), result.fiscalCode());
        assertEquals(account.getCurrency(), result.currency());
    }

    @Test
    void toGetAccountResult_shouldMapAllFields() {
        var account = sampleAccount();

        var result = mapper.toGetAccountResult(account);

        assertEquals(account.getUuid(), result.uuid());
        assertEquals(account.getEmail(), result.email());
        assertEquals(account.getStatus(), result.status());
    }

    @Test
    void toGetAccountBalanceResult_shouldMapBalanceAndCurrency() {
        var account = sampleAccount();

        var result = mapper.toGetAccountBalanceResult(account);

        assertEquals(0, account.getBalance().compareTo(result.balance()));
        assertEquals(account.getCurrency(), result.currency());
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
                .balance(new BigDecimal("120.00"))
                .currency("EUR")
                .status(AccountStatus.ACTIVE)
                .build();
    }
}

