package it.bank.bankcore.ledger.domain.mapper.cases;

import it.bank.bankcore.ledger.application.command.RecordDepositLedgerCommand;
import it.bank.bankcore.ledger.domain.enums.LedgerType;
import it.bank.bankcore.ledger.domain.model.LedgerEntry;
import it.bank.bankcore.shared.domain.DomainMapper;
import org.springframework.stereotype.Component;

@Component
public class DepositLedgerEntryMapper implements DomainMapper<RecordDepositLedgerCommand, LedgerEntry> {

    @Override
    public LedgerEntry toDomain(RecordDepositLedgerCommand command) {
        return LedgerEntry.builder()
                .accountId(command.accountId())
                .paymentId(command.paymentId())
                .type(LedgerType.CREDIT)
                .currency(command.currency())
                .amount(command.amount())
                .build();
    }
}
