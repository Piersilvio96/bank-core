package it.bank.bankcore.payment.api.controller;

import it.bank.bankcore.payment.api.mapper.DepositMapper;
import it.bank.bankcore.payment.api.mapper.ReversalMapper;
import it.bank.bankcore.payment.api.mapper.TransferMapper;
import it.bank.bankcore.payment.api.mapper.WithdrawMapper;
import it.bank.bankcore.payment.api.request.DepositRequest;
import it.bank.bankcore.payment.api.request.ReversalRequest;
import it.bank.bankcore.payment.api.request.TransferRequest;
import it.bank.bankcore.payment.api.request.WithdrawRequest;
import it.bank.bankcore.payment.api.response.DepositResponse;
import it.bank.bankcore.payment.api.response.ReversalResponse;
import it.bank.bankcore.payment.api.response.TransferResponse;
import it.bank.bankcore.payment.api.response.WithdrawResponse;
import it.bank.bankcore.payment.application.usecase.DepositUseCase;
import it.bank.bankcore.payment.application.usecase.ReversalUseCase;
import it.bank.bankcore.payment.application.usecase.TransferUseCase;
import it.bank.bankcore.payment.application.usecase.WithdrawUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final DepositMapper depositMapper;
    private final DepositUseCase depositUseCase;
    private final WithdrawMapper withdrawMapper;
    private final WithdrawUseCase withdrawUseCase;
    private final TransferMapper transferMapper;
    private final TransferUseCase transferUseCase;
    private final ReversalMapper reversalMapper;
    private final ReversalUseCase reversalUseCase;

    @PostMapping("/deposit")
    public ResponseEntity<DepositResponse> deposit(@Valid @RequestBody DepositRequest input)
    {
        var result = depositUseCase.execute(depositMapper.toCommand(input));
        return result.created()
                ? ResponseEntity.status(HttpStatus.CREATED).body(depositMapper.toResponse(result))
                : ResponseEntity.ok(depositMapper.toResponse(result));
    }

    @PostMapping("/withdraw")
    public ResponseEntity<WithdrawResponse> withdraw(@Valid @RequestBody WithdrawRequest input)
    {
        var result = withdrawUseCase.execute(withdrawMapper.toCommand(input));
        return result.created()
                ? ResponseEntity.status(HttpStatus.CREATED).body(withdrawMapper.toResponse(result))
                : ResponseEntity.ok(withdrawMapper.toResponse(result));
    }

    @PostMapping("/transfer")
    public ResponseEntity<TransferResponse> transfer(@Valid @RequestBody TransferRequest input)
    {
        var result = transferUseCase.execute(transferMapper.toCommand(input));
        return result.created()
                ? ResponseEntity.status(HttpStatus.CREATED).body(transferMapper.toResponse(result))
                : ResponseEntity.ok(transferMapper.toResponse(result));
    }

    @PostMapping("/reverse")
    public ResponseEntity<ReversalResponse> reverse(@Valid @RequestBody ReversalRequest input)
    {
        var result = reversalUseCase.execute(reversalMapper.toCommand(input));
        return result.created()
                ? ResponseEntity.status(HttpStatus.CREATED).body(reversalMapper.toResponse(result))
                : ResponseEntity.ok(reversalMapper.toResponse(result));
    }

}
