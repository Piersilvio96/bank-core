package it.bank.bankcore.account.api.controller;

import it.bank.bankcore.account.api.mapper.CreateAccountMapper;
import it.bank.bankcore.account.api.request.CreateAccountRequest;
import it.bank.bankcore.account.api.response.CreateAccountResponse;
import it.bank.bankcore.account.api.response.GetAccountBalanceResponse;
import it.bank.bankcore.account.api.response.GetAccountResponse;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import it.bank.bankcore.account.application.usecase.*;
import jakarta.validation.Valid;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/accounts")
public class AccountController {

    private final CreateAccountMapper createAccountMapper;
    private final CreateAccountUseCase createAccountUseCase;
    private final GetAccountUseCase getAccountUseCase;
    private final GetBalanceUseCase getBalanceUseCase;

    @PostMapping
    public CreateAccountResponse createAccount(
            @Valid @RequestBody CreateAccountRequest request
    ) {
        return createAccountUseCase.execute(createAccountMapper.toCommand(request));
    }

    @GetMapping("/{uuid}")
    public GetAccountResponse getAccount(
            @PathVariable String uuid
    ) {
        return getAccountUseCase.execute(uuid);
    }

    @GetMapping("/{uuid}/balance")
    public GetAccountBalanceResponse getAccountBalance(
            @PathVariable String uuid
    ) {
        return getBalanceUseCase.execute(uuid);
    }

}
