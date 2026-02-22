package com.snowk.blog.api.user.infrastructure.persistence.jpa;

import java.util.Optional;

import com.snowk.blog.api.user.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserJpaRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);
}
