package com.snowk.blog.api.user.infrastructure.persistence.adapter;

import com.snowk.blog.api.user.application.port.out.UserRepositoryPort;
import java.util.Optional;

import com.snowk.blog.api.user.domain.entity.User;
import com.snowk.blog.api.user.infrastructure.persistence.jpa.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserRepositoryAdapter implements UserRepositoryPort {

    private final UserJpaRepository userJpaRepository;

    @Override
    public User save(User user) {
        return userJpaRepository.save(user);
    }

    @Override
    public Optional<User> findById(Long userId) {
        return userJpaRepository.findById(userId);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return userJpaRepository.findByUsername(username);
    }
}
