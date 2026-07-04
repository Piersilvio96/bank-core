package it.bank.bankcore.account.api.controller;

import it.bank.bankcore.account.api.request.CreateAccountRequest;
import it.bank.bankcore.account.api.response.CreateAccountResponse;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import it.bank.bankcore.account.application.usecase.*;
import jakarta.validation.Valid;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/accounts")
public class AccountController {

    private final CreateAccountUseCase createAccountUseCase;

    @PostMapping
    public CreateAccountResponse createAccount(
            @Valid @RequestBody CreateAccountRequest request
    ) {
        return createAccountUseCase.execute(request);
    }


}
