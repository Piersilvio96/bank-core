package it.bank.bankcore.user.infrastructure.persistence;

import it.bank.bankcore.user.domain.model.User;
import it.bank.bankcore.user.domain.repository.UserRepository;
import it.bank.bankcore.user.infrastructure.persistence.mapper.UserJpaMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {
    private final UserJpaRepository userJpaRepository;
    private final UserJpaMapper userJpaMapper;

    @Override
    public User save(User user) {
        var savedEntity = userJpaRepository.save(userJpaMapper.toEntity(user));
        return userJpaMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<User> findByUuid(String uuid) {
        return userJpaRepository.findByUuid(uuid).map(userJpaMapper::toDomain);
    }

    @Override
    public boolean existsByUuid(String uuid) {
        return userJpaRepository.existsByUuid(uuid);
    }

    @Override
    public boolean existsByFiscalCodeOrEmail(String fiscalCode, String email) {
        return userJpaRepository.existsByFiscalCodeOrEmail(fiscalCode, email);
    }
}
