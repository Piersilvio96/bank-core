package it.bank.bankcore.ledger.domain.mapper.cases;

import it.bank.bankcore.ledger.application.command.RecordReversalLedgerCommand;
import it.bank.bankcore.ledger.domain.enums.LedgerType;
import it.bank.bankcore.ledger.domain.model.LedgerEntry;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

@Component
public class ReversalLedgerEntryMapper{

    public LedgerEntry toDomain(RecordReversalLedgerCommand command, LedgerEntry ledgerEntry) {
        return LedgerEntry.builder()
                .accountId(ledgerEntry.getAccountId())
                .paymentId(command.reversalId())
                .type(ObjectUtils.nullSafeEquals(ledgerEntry.getType(), LedgerType.DEBIT) ? LedgerType.CREDIT : LedgerType.DEBIT)
                .currency(ledgerEntry.getCurrency())
                .amount(ledgerEntry.getAmount())
                .build();
    }
}
