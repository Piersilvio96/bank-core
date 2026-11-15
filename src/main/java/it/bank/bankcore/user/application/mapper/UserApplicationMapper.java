package it.bank.bankcore.user.application.mapper;

import it.bank.bankcore.shared.application.ApplicationMapper;
import it.bank.bankcore.user.application.command.CreateUserCommand;
import it.bank.bankcore.user.application.result.UserResult;
import it.bank.bankcore.user.domain.enums.UserStatus;
import it.bank.bankcore.user.domain.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserApplicationMapper implements ApplicationMapper<CreateUserCommand, User, UserResult> {

    @Override
    public User toDomain(CreateUserCommand command) {
        return User.builder()
                .firstName(command.firstName())
                .lastName(command.lastName())
                .email(command.email())
                .phoneNumber(command.phoneNumber())
                .fiscalCode(command.fiscalCode())
                .city(command.city())
                .state(command.state())
                .country(command.country())
                .status(UserStatus.ACTIVE)
                .build();
    }

    @Override
    public UserResult toResult(User domain) {
        return new UserResult(
                domain.getUuid(),
                domain.getFirstName(),
                domain.getLastName(),
                domain.getEmail(),
                domain.getFiscalCode(),
                domain.getPhoneNumber(),
                domain.getCity(),
                domain.getState(),
                domain.getCountry(),
                domain.getStatus(),
                domain.getCreatedAt()
        );
    }
}
