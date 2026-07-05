package it.bank.bankcore.ledger.domain.mapper.cases;

import it.bank.bankcore.ledger.application.command.RecordTransferLedgerCommand;
import it.bank.bankcore.ledger.domain.enums.LedgerType;
import it.bank.bankcore.ledger.domain.model.LedgerEntry;
import it.bank.bankcore.shared.domain.DomainMapper;
import org.springframework.stereotype.Component;

@Component
public class TransferFromLedgerEntryMapper implements DomainMapper<RecordTransferLedgerCommand, LedgerEntry> {

    @Override
    public LedgerEntry toDomain(RecordTransferLedgerCommand command) {
        return LedgerEntry.builder()
                .accountId(command.sourceAccountId())
                .paymentId(command.paymentId())
                .type(LedgerType.DEBIT)
                .currency(command.currency())
                .amount(command.amount())
                .build();
    }
}
