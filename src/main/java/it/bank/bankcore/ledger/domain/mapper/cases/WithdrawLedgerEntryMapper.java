package it.bank.bankcore.ledger.domain.mapper.cases;

import it.bank.bankcore.ledger.application.command.RecordWithdrawLedgerCommand;
import it.bank.bankcore.ledger.domain.enums.LedgerType;
import it.bank.bankcore.ledger.domain.model.LedgerEntry;
import it.bank.bankcore.shared.domain.DomainMapper;
import org.springframework.stereotype.Component;

@Component
public class WithdrawLedgerEntryMapper implements DomainMapper<RecordWithdrawLedgerCommand, LedgerEntry> {

    @Override
    public LedgerEntry toDomain(RecordWithdrawLedgerCommand command) {
        return LedgerEntry.builder()
                .accountId(command.accountId())
                .paymentId(command.paymentId())
                .type(LedgerType.DEBIT)
                .currency(command.currency())
                .amount(command.amount())
                .build();
    }
}
