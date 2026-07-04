package it.bank.bankcore.account.domain.specification;

import it.bank.bankcore.account.domain.criteria.CreateAccountCriteria;
import it.bank.bankcore.account.domain.model.AccountEntity;
import org.springframework.data.jpa.domain.Specification;

public class CreateAccountCriteriaSpecification {

    public static Specification<AccountEntity> createSpecification(CreateAccountCriteria criteria) {
        return (root, query, criteriaBuilder) -> {
            return criteriaBuilder.or(
                criteriaBuilder.equal(root.get("fiscalCode"), criteria.getFiscalCode()),
                criteriaBuilder.equal(root.get("email"), criteria.getEmail())
            );
        };
    }
}
