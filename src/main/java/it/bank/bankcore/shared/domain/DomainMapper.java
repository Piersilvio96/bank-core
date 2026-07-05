package it.bank.bankcore.shared.domain;

public interface DomainMapper <C, D>
{
    C toCommand(D domain);
    D toDomain(C command);
}
