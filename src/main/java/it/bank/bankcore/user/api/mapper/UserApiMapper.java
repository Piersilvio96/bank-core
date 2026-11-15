package it.bank.bankcore.user.api.mapper;

import it.bank.bankcore.shared.api.ApiInMapper;
import it.bank.bankcore.shared.api.ApiOutMapper;
import it.bank.bankcore.user.api.request.CreateUserRequest;
import it.bank.bankcore.user.api.response.UserResponse;
import it.bank.bankcore.user.application.command.CreateUserCommand;
import it.bank.bankcore.user.application.result.UserResult;
import org.springframework.stereotype.Component;

@Component
public class UserApiMapper implements
        ApiInMapper<CreateUserRequest, CreateUserCommand>,
        ApiOutMapper<UserResponse, UserResult>
{
    @Override
    public CreateUserCommand toCommand(CreateUserRequest request) {
        return new CreateUserCommand(
                request.firstName(),
                request.lastName(),
                request.email(),
                request.phoneNumber(),
                request.fiscalCode(),
                request.city(),
                request.state(),
                request.country()
        );
    }

    @Override
    public UserResponse toResponse(UserResult result) {
        return new UserResponse(
                result.uuid(),
                result.firstName(),
                result.lastName(),
                result.email(),
                result.fiscalCode(),
                result.phoneNumber(),
                result.city(),
                result.state(),
                result.country(),
                result.status(),
                result.createdAt()
        );
    }
}
