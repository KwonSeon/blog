package com.snowk.blog.api.user.infrastructure.persistence.adapter;

import static org.mockito.Mockito.verify;

import com.snowk.blog.api.user.domain.entity.User;
import com.snowk.blog.api.user.infrastructure.persistence.jpa.UserJpaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserRepositoryAdapterTest {

    @Mock
    private UserJpaRepository userJpaRepository;

    @InjectMocks
    private UserRepositoryAdapter userRepositoryAdapter;

    @Test
    @DisplayName("사용자 저장은 JPA 리포지토리에 위임된다")
    void save_delegatesToJpaRepository() {
        User user = org.mockito.Mockito.mock(User.class);

        userRepositoryAdapter.save(user);

        verify(userJpaRepository).save(user);
    }

    @Test
    @DisplayName("사용자 ID 조회는 JPA 리포지토리에 위임된다")
    void findById_delegatesToJpaRepository() {
        userRepositoryAdapter.findById(1L);

        verify(userJpaRepository).findById(1L);
    }

    @Test
    @DisplayName("사용자명 조회는 JPA 리포지토리에 위임된다")
    void findByUsername_delegatesToJpaRepository() {
        userRepositoryAdapter.findByUsername("admin");

        verify(userJpaRepository).findByUsername("admin");
    }
}
