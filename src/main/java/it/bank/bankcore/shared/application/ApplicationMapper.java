package it.bank.bankcore.shared.application;

public interface ApplicationMapper <C, D, R>
{
    D toDomain(C command);
    R toResult(D domain);
}
