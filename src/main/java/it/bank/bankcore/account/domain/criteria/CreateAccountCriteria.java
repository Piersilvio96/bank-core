package it.bank.bankcore.account.domain.criteria;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateAccountCriteria {
    private String fiscalCode;
    private String email;
}
