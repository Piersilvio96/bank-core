package it.bank.bankcore.account.infrastructure.persistence.specification;

import it.bank.bankcore.account.infrastructure.persistence.AccountJpaEntity;
import org.springframework.data.jpa.domain.Specification;

public class GetAccountCriteriaSpecification {

    public static Specification<AccountJpaEntity> createSpecification(String uuid) {
        return (root, query, criteriaBuilder) -> {
            if (uuid != null) {
                return criteriaBuilder.equal(root.get("uuid"), uuid);
            } else {
                return criteriaBuilder.conjunction();
            }
        };
    }

}
