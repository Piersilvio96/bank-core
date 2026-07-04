package it.bank.bankcore.shared.api;

import lombok.Data;

@Data
public class ErrorResponse {
    private String message;
    private String details;
}
