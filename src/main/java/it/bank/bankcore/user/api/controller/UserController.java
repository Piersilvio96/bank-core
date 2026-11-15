package it.bank.bankcore.user.api.controller;

import it.bank.bankcore.user.api.mapper.UserApiMapper;
import it.bank.bankcore.user.api.request.CreateUserRequest;
import it.bank.bankcore.user.api.response.UserResponse;
import it.bank.bankcore.user.application.query.GetUserQuery;
import it.bank.bankcore.user.application.usecase.CreateUserUseCase;
import it.bank.bankcore.user.application.usecase.GetUserUseCase;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {
    private final UserApiMapper userApiMapper;
    private final CreateUserUseCase createUserUseCase;
    private final GetUserUseCase getUserUseCase;

    @PostMapping
    public ResponseEntity<UserResponse> createUser(
            @Valid @RequestBody CreateUserRequest request
    ) {
        var result = createUserUseCase.execute(userApiMapper.toCommand(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(userApiMapper.toResponse(result));
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<UserResponse> getUser(
            @PathVariable String uuid
    ) {
        var result = getUserUseCase.execute(new GetUserQuery(uuid));
        return ResponseEntity.ok(userApiMapper.toResponse(result));
    }
}
