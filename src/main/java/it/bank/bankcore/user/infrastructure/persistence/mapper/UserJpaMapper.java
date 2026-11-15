package it.bank.bankcore.user.infrastructure.persistence.mapper;

import it.bank.bankcore.shared.infrastructure.JpaMapper;
import it.bank.bankcore.user.domain.model.User;
import it.bank.bankcore.user.infrastructure.persistence.UserJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class UserJpaMapper implements JpaMapper<User, UserJpaEntity> {

    @Override
    public User toDomain(UserJpaEntity entity) {
        return User.builder()
                .id(entity.getId())
                .uuid(entity.getUuid())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .firstName(entity.getFirstName())
                .lastName(entity.getLastName())
                .email(entity.getEmail())
                .fiscalCode(entity.getFiscalCode())
                .phoneNumber(entity.getPhoneNumber())
                .city(entity.getCity())
                .state(entity.getState())
                .country(entity.getCountry())
                .status(entity.getStatus())
                .build();
    }

    @Override
    public UserJpaEntity toEntity(User domain) {
        return UserJpaEntity.builder()
                .id(domain.getId())
                .uuid(domain.getUuid())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .firstName(domain.getFirstName())
                .lastName(domain.getLastName())
                .email(domain.getEmail())
                .fiscalCode(domain.getFiscalCode())
                .phoneNumber(domain.getPhoneNumber())
                .city(domain.getCity())
                .state(domain.getState())
                .country(domain.getCountry())
                .status(domain.getStatus())
                .build();
    }
}
