package it.bank.bankcore.user.domain.model;

import it.bank.bankcore.shared.domain.Base;
import it.bank.bankcore.user.domain.enums.UserStatus;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

/**
 * The physical person owning one or more accounts.
 */
@EqualsAndHashCode(callSuper = true)
@Getter
@AllArgsConstructor
@SuperBuilder
public class User extends Base {
    private String firstName;
    private String lastName;
    private String email;
    private String fiscalCode;
    private String phoneNumber;
    private String city;
    private String state;
    private String country;
    private UserStatus status;
}
