package it.bank.bankcore.ledger.domain.mapper;

import it.bank.bankcore.ledger.application.command.RecordDepositLedgerCommand;
import it.bank.bankcore.ledger.application.command.RecordReversalLedgerCommand;
import it.bank.bankcore.ledger.application.command.RecordTransferLedgerCommand;
import it.bank.bankcore.ledger.application.command.RecordWithdrawLedgerCommand;
import it.bank.bankcore.ledger.domain.mapper.cases.*;
import it.bank.bankcore.ledger.domain.model.LedgerEntry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LedgerDomainMapper {

    private final DepositLedgerEntryMapper depositLedgerEntryMapper;
    private final TransferFromLedgerEntryMapper transferFromLedgerEntryMapper;
    private final TransferToLedgerEntryMapper transferToLedgerEntryMapper;
    private final WithdrawLedgerEntryMapper withdrawLedgerEntryMapper;
    private final ReversalLedgerEntryMapper reversalLedgerEntryMapper;


    public LedgerEntry toDomain(RecordDepositLedgerCommand command) {
        return depositLedgerEntryMapper.toDomain(command);
    }

    public LedgerEntry toDomain(RecordWithdrawLedgerCommand command) {
        return withdrawLedgerEntryMapper.toDomain(command);
    }

    public LedgerEntry toDomain(RecordTransferLedgerCommand command, Boolean source) {
        if (source) {
            return transferFromLedgerEntryMapper.toDomain(command);
        }
        return transferToLedgerEntryMapper.toDomain(command);
    }

    public LedgerEntry toDomain(RecordReversalLedgerCommand command, LedgerEntry ledgerEntry) {
        return reversalLedgerEntryMapper.toDomain(command, ledgerEntry);
    }







}
