package it.bank.bankcore.user.application.usecase;

import it.bank.bankcore.user.application.command.CreateUserCommand;
import it.bank.bankcore.user.application.mapper.UserApplicationMapper;
import it.bank.bankcore.user.domain.enums.UserStatus;
import it.bank.bankcore.user.domain.exception.UserAlreadyExistsException;
import it.bank.bankcore.user.domain.exception.UserNotFoundException;
import it.bank.bankcore.user.domain.model.User;
import it.bank.bankcore.user.domain.repository.UserRepository;
import it.bank.bankcore.user.application.query.GetUserQuery;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateUserUseCaseTest {

    @Mock
    private UserRepository userRepository;

    private final UserApplicationMapper mapper = new UserApplicationMapper();

    @Test
    void execute_shouldThrowWhenFiscalCodeOrEmailAlreadyUsed() {
        var command = sampleCommand();
        when(userRepository.existsByFiscalCodeOrEmail(command.fiscalCode(), command.email())).thenReturn(true);

        var useCase = new CreateUserUseCase(userRepository, mapper);

        assertThrows(UserAlreadyExistsException.class, () -> useCase.execute(command));
        verify(userRepository, never()).save(any());
    }

    @Test
    void execute_shouldCreateActiveUser() {
        var command = sampleCommand();
        when(userRepository.existsByFiscalCodeOrEmail(command.fiscalCode(), command.email())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var result = new CreateUserUseCase(userRepository, mapper).execute(command);

        assertEquals("Mario", result.firstName());
        assertEquals("mario.rossi@example.com", result.email());
        assertEquals("RSSMRA80A01H501U", result.fiscalCode());
        assertEquals(UserStatus.ACTIVE, result.status());
    }

    @Test
    void getUser_shouldReturnUserWhenFound() {
        var user = mapper.toDomain(sampleCommand());
        when(userRepository.findByUuid(user.getUuid())).thenReturn(Optional.of(user));

        var result = new GetUserUseCase(userRepository, mapper).execute(new GetUserQuery(user.getUuid()));

        assertEquals(user.getUuid(), result.uuid());
        assertEquals("Rossi", result.lastName());
    }

    @Test
    void getUser_shouldThrowWhenMissing() {
        when(userRepository.findByUuid("missing")).thenReturn(Optional.empty());

        var useCase = new GetUserUseCase(userRepository, mapper);

        assertThrows(UserNotFoundException.class, () -> useCase.execute(new GetUserQuery("missing")));
    }

    private CreateUserCommand sampleCommand() {
        return new CreateUserCommand(
                "Mario", "Rossi", "mario.rossi@example.com", "+393331234567", "RSSMRA80A01H501U",
                "Rome", "RM", "Italy"
        );
    }
}
