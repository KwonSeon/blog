package com.snowk.blog.api.user.infrastructure.persistence.jpa;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.snowk.blog.api.support.ReflectionEntityFactory;
import com.snowk.blog.api.user.domain.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class UserJpaRepositoryTest {

    @Autowired
    private UserJpaRepository userJpaRepository;

    @Test
    @DisplayName("사용자명으로 사용자를 조회할 수 있다")
    void findByUsername_returnsSavedUser() {
        User user = createUser("admin", "admin@example.com");

        userJpaRepository.save(user);

        assertThat(userJpaRepository.findByUsername("admin")).isPresent();
    }

    @Test
    @DisplayName("동일한 사용자명은 저장할 수 없다")
    void save_duplicateUsername_throwsDataIntegrityViolation() {
        userJpaRepository.saveAndFlush(createUser("duplicated-user", "a@example.com"));

        assertThatThrownBy(() -> userJpaRepository.saveAndFlush(createUser("duplicated-user", "b@example.com")))
            .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    @DisplayName("비밀번호 해시가 없으면 사용자를 저장할 수 없다")
    void save_nullPasswordHash_throwsDataIntegrityViolation() {
        User user = createUser("no-password-user", "np@example.com");
        ReflectionEntityFactory.setField(user, "passwordHash", null);

        assertThatThrownBy(() -> userJpaRepository.saveAndFlush(user))
            .isInstanceOf(DataIntegrityViolationException.class);
    }

    private User createUser(String username, String email) {
        User user = ReflectionEntityFactory.instantiate(User.class);
        ReflectionEntityFactory.setField(user, "username", username);
        ReflectionEntityFactory.setField(user, "email", email);
        ReflectionEntityFactory.setField(user, "passwordHash", "hashed-password");
        return user;
    }
}
