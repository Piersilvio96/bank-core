package it.bank.bankcore.payment.api.controller;

import it.bank.bankcore.payment.api.request.DepositRequest;
import it.bank.bankcore.payment.api.response.DepositResponse;
import it.bank.bankcore.payment.application.usecase.DepositUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final DepositUseCase depositUseCase;

    @PostMapping("/deposit")
    public DepositResponse deposit(@Valid @RequestBody DepositRequest input)
    {
        var response = depositUseCase.execute(input);
        return response;
    }


}
