package it.bank.bankcore.shared.infrastructure;

public interface JpaMapper <D,E> {

    D toDomain(E entity);
    E toEntity(D domain);

}
