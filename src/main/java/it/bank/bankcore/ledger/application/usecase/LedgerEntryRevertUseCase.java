package it.bank.bankcore.ledger.application.usecase;

import it.bank.bankcore.ledger.application.command.RecordReversalLedgerCommand;
import it.bank.bankcore.ledger.domain.mapper.LedgerDomainMapper;
import it.bank.bankcore.ledger.domain.repository.LedgerEntryRepository;
import it.bank.bankcore.shared.application.UseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LedgerEntryRevertUseCase implements UseCase<RecordReversalLedgerCommand, Void> {

    private final LedgerEntryRepository ledgerEntryRepository;
    private final LedgerDomainMapper ledgerDomainMapper;

    @Override
    public Void execute(RecordReversalLedgerCommand input) {
        var ledgerEntries = ledgerEntryRepository.findByPaymentId(input.paymentId());
        if (ledgerEntries.isEmpty()) {
            throw new RuntimeException("Ledger entry with payment id " + input.paymentId() + " not found");
        }

        var toBeStored = ledgerEntries.stream()
                .map(ledgerEntry -> ledgerDomainMapper.toDomain(input, ledgerEntry))
                .toList();

        ledgerEntryRepository.saveAll(toBeStored);

        return null;

    }
}
