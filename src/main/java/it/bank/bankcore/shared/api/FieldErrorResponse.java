package it.bank.bankcore.shared.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FieldErrorResponse {
    private String field;
    private String message;
    private Object rejectedValue;

}
