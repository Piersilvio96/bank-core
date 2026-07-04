package it.bank.bankcore.account.api.controller;

import it.bank.bankcore.account.api.request.CreateAccountRequest;
import it.bank.bankcore.account.api.response.CreateAccountResponse;
import it.bank.bankcore.account.api.response.GetAccountResponse;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import it.bank.bankcore.account.application.usecase.*;
import jakarta.validation.Valid;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/accounts")
public class AccountController {

    private final CreateAccountUseCase createAccountUseCase;
    private final GetAccountUseCase getAccountUseCase;

    @PostMapping
    public CreateAccountResponse createAccount(
            @Valid @RequestBody CreateAccountRequest request
    ) {
        return createAccountUseCase.execute(request);
    }

    @GetMapping("/{uuid}")
    public GetAccountResponse getAccount(
            @PathVariable String uuid
    ) {
        return getAccountUseCase.execute(uuid);
    }


}
