package it.bank.bankcore.shared.api;

public interface ApiInMapper <R,C> {
    C toCommand(R request);
}
