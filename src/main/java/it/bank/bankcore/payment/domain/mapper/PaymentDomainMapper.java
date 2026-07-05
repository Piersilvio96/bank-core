package it.bank.bankcore.payment.domain.mapper;

import it.bank.bankcore.payment.application.command.DepositCommand;
import it.bank.bankcore.payment.application.command.TransferCommand;
import it.bank.bankcore.payment.application.command.WithdrawCommand;
import it.bank.bankcore.payment.domain.mapper.cases.DepositDomainMapper;
import it.bank.bankcore.payment.domain.mapper.cases.TransferDomainMapper;
import it.bank.bankcore.payment.domain.mapper.cases.WithdrawDomainMapper;
import it.bank.bankcore.payment.domain.model.Payment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentDomainMapper {

    private final DepositDomainMapper depositDomainMapper;
    private final TransferDomainMapper transferDomainMapper;
    private final WithdrawDomainMapper withdrawDomainMapper;

    public Payment toDomain(DepositCommand command) {
        return depositDomainMapper.toDomain(command);
    }

    public Payment toDomain(TransferCommand command) {
        return transferDomainMapper.toDomain(command);
    }

    public Payment toDomain(WithdrawCommand command) {
        return withdrawDomainMapper.toDomain(command);
    }

}

