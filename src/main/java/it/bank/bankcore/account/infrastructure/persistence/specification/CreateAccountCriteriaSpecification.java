package it.bank.bankcore.account.infrastructure.persistence.specification;

import it.bank.bankcore.account.domain.criteria.CreateAccountCriteria;
import it.bank.bankcore.account.infrastructure.persistence.AccountJpaEntity;
import org.springframework.data.jpa.domain.Specification;

public class CreateAccountCriteriaSpecification {

    public static Specification<AccountJpaEntity> createSpecification(CreateAccountCriteria criteria) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.or(
            criteriaBuilder.equal(root.get("fiscalCode"), criteria.getFiscalCode()),
            criteriaBuilder.equal(root.get("email"), criteria.getEmail())
        );
    }
}
