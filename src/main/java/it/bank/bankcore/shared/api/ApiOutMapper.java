package it.bank.bankcore.shared.api;

public interface ApiOutMapper <Rp, Rs> {
    Rp toResponse(Rs result);
}
