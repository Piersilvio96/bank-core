package it.bank.bankcore.shared.domain;

public interface DomainMapper <C, D>
{
    D toDomain(C command);
}
