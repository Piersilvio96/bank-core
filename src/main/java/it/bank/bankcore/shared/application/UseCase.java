package it.bank.bankcore.shared.application;

public interface UseCase<I, O> {
    O execute(I input);
}
