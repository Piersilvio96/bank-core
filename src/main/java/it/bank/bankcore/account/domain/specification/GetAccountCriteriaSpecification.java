package it.bank.bankcore.account.domain.specification;

import it.bank.bankcore.account.domain.model.AccountEntity;
import org.springframework.data.jpa.domain.Specification;

public class GetAccountCriteriaSpecification {

    public static Specification<AccountEntity> createSpecification(String uuid) {
        return (root, query, criteriaBuilder) -> {
            if (uuid != null) {
                return criteriaBuilder.equal(root.get("uuid"), uuid);
            } else {
                return criteriaBuilder.conjunction();
            }
        };
    }

}
