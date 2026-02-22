package com.snowk.blog.api.user.application.port.out;

import com.snowk.blog.api.user.domain.entity.User;

import java.util.Optional;

public interface UserRepositoryPort {

    User save(User user);

    Optional<User> findById(Long userId);

    Optional<User> findByUsername(String username);
}
