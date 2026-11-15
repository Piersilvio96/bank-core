package it.bank.bankcore.account.api.controller;

import it.bank.bankcore.account.api.mapper.CreateAccountMapper;
import it.bank.bankcore.account.api.mapper.GetAccountMapper;
import it.bank.bankcore.account.api.mapper.GetBalanceMapper;
import it.bank.bankcore.account.api.request.CreateAccountRequest;
import it.bank.bankcore.account.api.response.CreateAccountResponse;
import it.bank.bankcore.account.api.response.GetAccountBalanceResponse;
import it.bank.bankcore.account.api.response.GetAccountResponse;
import it.bank.bankcore.account.application.query.GetAccountQuery;
import it.bank.bankcore.account.application.query.GetBalanceQuery;
import it.bank.bankcore.account.application.query.ListAccountsQuery;
import it.bank.bankcore.account.application.usecase.CreateAccountUseCase;
import it.bank.bankcore.account.application.usecase.GetAccountUseCase;
import it.bank.bankcore.account.application.usecase.GetBalanceUseCase;
import it.bank.bankcore.account.application.usecase.ListAccountsUseCase;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/accounts")
public class AccountController {

    private final CreateAccountMapper createAccountMapper;
    private final GetAccountMapper getAccountMapper;
    private final GetBalanceMapper getBalanceMapper;

    private final CreateAccountUseCase createAccountUseCase;
    private final GetAccountUseCase getAccountUseCase;
    private final GetBalanceUseCase getBalanceUseCase;
    private final ListAccountsUseCase listAccountsUseCase;

    @PostMapping
    public ResponseEntity<CreateAccountResponse> createAccount(
            @Valid @RequestBody CreateAccountRequest request
    ) {
        var result = createAccountUseCase.execute(createAccountMapper.toCommand(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(createAccountMapper.toResponse(result));
    }

    @GetMapping
    public ResponseEntity<List<GetAccountResponse>> listAccounts(
            @RequestParam String userUuid
    ) {
        var results = listAccountsUseCase.execute(new ListAccountsQuery(userUuid));
        return ResponseEntity.ok(getAccountMapper.toResponses(results));
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<GetAccountResponse> getAccount(
            @PathVariable String uuid
    ) {
        var result = getAccountUseCase.execute(new GetAccountQuery(uuid));
        return ResponseEntity.ok(getAccountMapper.toResponse(result));
    }

    @GetMapping("/{uuid}/balance")
    public ResponseEntity<GetAccountBalanceResponse> getAccountBalance(
            @PathVariable String uuid
    ) {
        var result = getBalanceUseCase.execute(new GetBalanceQuery(uuid));
        return ResponseEntity.ok(getBalanceMapper.toResponse(result));
    }
}
