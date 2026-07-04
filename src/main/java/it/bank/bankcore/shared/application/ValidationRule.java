package it.bank.bankcore.shared.application;

public interface ValidationRule<T> {
    void validate(T object);
}
