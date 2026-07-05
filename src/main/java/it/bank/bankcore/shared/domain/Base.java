package it.bank.bankcore.shared.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class Base {
    private Long id;
    @Builder.Default
    private String uuid = UUID.randomUUID().toString();
    private Long createdAt;
    private Long updatedAt;
}
