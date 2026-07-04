package it.bank.bankcore.account.api.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GetAccountResponse {
    private String uuid;
    private String firstName;
    private String lastName;
    private String email;
    private String fiscalCode;
    private String phoneNumber;
    private String city;
    private String state;
    private String country;
    private BigDecimal balance;
    private String currency;
}
